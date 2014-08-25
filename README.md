PlayBootstrap
=============

Run your personal configuration, type in your sbt prompt :
[PlayBoostrap] $ run -Dconfig.resource=personal.conf

Create an admin
$ curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X POST http://localhost:9000/api/admins -d '{"firstName":"fN","lastName":"lN","email":"e@e.com","active":true,"creation":1398435341051}'
