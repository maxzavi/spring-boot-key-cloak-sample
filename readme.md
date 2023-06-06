# API Spring Boot with Keycloak tokens

Create maven project, with **Spring Initilizr**, Spring Boot 3.0.6, java 17, dependencies:
- Spring Web
- Lombok
- Sring Boot DevTools

## OpenAPIDefinition

Add dependency maven, by spring boot 3, use https://springdoc.org/v2/

```xml
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.1.0</version>
		</dependency>
```

view in http://localhost:8080/swagger-ui/index.html

In main class, use **OpenAPIDefinition** annotation

```java
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Product API with KeyCloak", version = "1.0.0"))

public class KeycloaksampleApplication {
```

In RestController, use **Tag** annotation

```java
@Tag(name = "Login", description = "Login API")
```

## KeyCloak

Run keyCloak in 8082 port

```cmd
docker run -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:21.1.1 start-dev
```

Load app, using http://localhost:8082/admin and setup:

- Create realm **testrealm**
- In real created, add user: set password **no temporary**; user1/mypassword1
- Create client **myclient**
Test using curl:
```sh
curl --location 'http://localhost:8082/realms/testrealm/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=myclient' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'scope=openid' \
--data-urlencode 'username=user1' \
--data-urlencode 'password=mypassword1'
```
Get UserInto, replace token 

```sh
curl --location 'http://localhost:8082/realms/testrealm/protocol/openid-connect/userinfo' \
--header 'Authorization: Bearer xxxxxxxx'
```
- In created client **myclient**, scope: “Client Scope”, in **myclient-dedicated** -> **Add predefined mapper** -> **realm roles**, check “Add to userinfo”

- In **Realm roles** create roles **USER** and **ADMIN**

- Asign rol **USER** to **user1**

## Spring Boot Project

Implement login service, and controller, curl:

- LoginController, path **/api/v1/login**

```java
@RequestMapping("/api/v1/login")
```

- KeyCloakService, RestTemplate, ProductController


```sh
curl --location 'http://localhost:8080/api/v1/login' \
--header 'Content-Type: application/json' \
--data '{
    "username":"user1",
    "password":"mypassword1"
}'
```

## Docker

Create dockerfile, using image base **eclipse-temurin:17-jre-alpine**

```docker
FROM --platform=linux/x86_64 eclipse-temurin:17-jre-alpine
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/keycloaksample-0.0.1-SNAPSHOT.jar keycloaksample.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar keycloaksample.jar
```

Compile spring boot project using maven

```sh
 mvn clean package
```

Create image docker, replacing DOCKERHUB_ID and VERSION variables:

```cmd
export DOCKERHUB_ID=mzavaletav
export VERSION=1.2
```

```cmd
docker build -t ${DOCKERHUB_ID}/springboot-keycloak-demo:${VERSION} .
docker push ${DOCKERHUB_ID}/springboot-keycloak-demo:${VERSION}
```

## Kubernetes

Check exists namespace **keycloak-srpingboot-demo**:

```sh
kubectl get ns
```

if exists, remove namespace:

```sh
kubectl delete ns keycloak-srpingboot-demo
```

Create namespaces

```sh
 kubectl apply -f k8s/01-namespace.yml
```

Set default namespace

```sh
kubectl config set-context --current --namespace=keycloak-srpingboot-demo
```
Create keycloak service:
```sh
kubectl apply -f k8s/02-keycloak.yml
```
Get port number type nodeport:

```cmd
kubectl get services
```
Using port number in PORT_NUMBER, access 

http://localhost:{PORT_NUMBER}

Configure realm, user, client like initial steps 

In **k8s/03-springboot-test.yml** change spec.template.spec.containers.image with docker image url (public), and set variable value **keycloak__url-base** with CLUSTE-IP of keycloak-nodeport service, port 8080: **http://10.111.251.41:8080**

```yml
spec:
  selector:
    matchLabels:
      app: springbootdemo-keycloak
  replicas: 1
  template:
    metadata:
      labels:
        app:  springbootdemo-keycloak
      annotations:
        repo: n/a
        swagger: n/a
    spec:
      containers:
      - name: springbootdemo-keycloak
        image:  mzavaletav/springboot-keycloak-demo:1.1
        env:
        - name: keycloak__url-base
          value: http://10.111.251.41:8080
```

Deploy using kubectl command:

```cmd
kubectl apply -f k8s/03-springboot-test.yml
```



# Demo full

1. Create namespace


```sh
 kubectl apply -f k8s/01-namespace.yml
```

Set default namespace

```sh
kubectl config set-context --current --namespace=keycloak-srpingboot-demo
```
Create keycloak service:
```sh
kubectl apply -f k8s/02-keycloak.yml
```
Get port number type nodeport:

```cmd
kubectl get services
```
Using port number in PORT_NUMBER, access 

http://localhost:{PORT_NUMBER}

- Create realm **testrealm**
- In real created, add user: set password **no temporary**; admintest/admintest$
- Create client **myclient**
- In created client **myclient**, scope: “Client Scope”, in **myclient-dedicated** -> **Add predefined mapper** -> **realm roles**, check “Add to userinfo”

- In **Realm roles** create roles **USER** and **ADMIN**

- Asign rol **USER**, and **ADMIN** to **admintest**

- Create realm **prodrealm**
- In real created, add user: set password **no temporary**; adminprod/4dm1npR0d$
- Create client **myclient**
- In created client **myclient**, scope: “Client Scope”, in **myclient-dedicated** -> **Add predefined mapper** -> **realm roles**, check “Add to userinfo”

- In **Realm roles** create roles **USER** and **ADMIN**

- Asign rol **USER**, and **ADMIN** to **adminprod**


In **k8s/03-springboot-test.yml** and **k8s/04-springboot-prod.yml** change spec.template.spec.containers.image with docker image url (public), and set variable value **keycloak__url-base** with CLUSTE-IP of keycloak-nodeport service, port 8080: **http://10.111.251.41:8080**


Deploy using kubectl command:

```sh
kubectl apply -f k8s/03-springboot-test.yml
```
```sh
kubectl apply -f k8s/04-springboot-prod.yml
```
get port by realm by environment:

Check enpoints in swagger, using port

http://localhost:31468/swagger-ui/index.html



Import curl from swagger and create request **login-test** in collection **main** y folder **security**

Duplicate request, and save **login-prod**, change url, username and password -- production realm

Create request **login**, create Environments and change values


In Keycloak admin console change access token lifespan token section to 30 minutes, in **Realm Settings** -> **Tokens** ->
**Access Token Lifespan** by test and prod realm.

Send login request and review in postman

Import get products en folder product and create request get product-test, change Authorization header

Import post product en folder product and create request product-test, change Authorization header, execute and check 
in get product-test

Repeat in production

Create environments test and prod and creat one request by login

Create one rquest get product, define variable access_token

Check prod and test, kill sessions in keycloak and test

Add Test in postman

```javascript
var data = JSON.parse(responseBody)
postman.setEnvironmentVariable("access_token",data.access_token)
```

In other methods add **Pre-request script**

```javascript
var  url =  pm.environment.get('url')
var  username =  pm.environment.get('username')
var  password =  pm.environment.get('password')

const echoPostRequest = {
  url: url+'/api/v1/login',
  method: 'POST',
  header: 'Content-Type:application/json',
  body: {
    mode: 'application/json',
    raw: JSON.stringify(
{
  "username": username,
  "password": password
})
  }
};
var getToken = true;

pm.sendRequest(echoPostRequest, function (err, res) {
    if (err === null) {
        var responseJson = res.json();
        pm.environment.set('access_token', responseJson.access_token)
    }
});
```

Other version

```javascript
var  url =  pm.environment.get('url')
var  username =  pm.environment.get('username')
var  password =  pm.environment.get('password')
const echoPostRequest = {
  url: url+'/api/v1/login',
  method: 'POST',
  header: 'Content-Type:application/json',
  body: {
    mode: 'application/json',
    raw: JSON.stringify(
{
  "username": username,
  "password": password
})
  }
};
var getToken = true;

if (!pm.environment.get('accessTokenExpiry') ) {
    console.log('Token or expiry date are missing')
} else if (pm.environment.get('accessTokenExpiry') <= (new Date()).getTime()) {
    console.log('Token is expired')
} else {
    getToken = false;
    console.log('Token and expiry date are all good');
}
if (getToken === true) {
    pm.sendRequest(echoPostRequest, function (err, res) {
        if (err === null) {
            var responseJson = res.json();
            pm.environment.set('access_token', responseJson.access_token)
    
            var expiryDate = new Date();
            expiryDate.setSeconds(expiryDate.getSeconds() + responseJson.expires_in);
            pm.environment.set('accessTokenExpiry', expiryDate.getTime());
        }
    });
}```