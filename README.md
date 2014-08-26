PlayBootstrap
=============

1- Clone the repo
2- Run SBT (>= 0.13)
3- Run the Play application
=> You have a working web application with a minimal set of common functionnalities that you can start extend

Run your personal configuration, type in your sbt prompt :
[PlayBoostrap] $ run -Dconfig.resource=personal.conf

Create an admin
$ curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X POST http://localhost:9000/api/admins -d '{"firstName":"fN","lastName":"lN","email":"e@e.com","active":true,"creation":1398435341051}'
