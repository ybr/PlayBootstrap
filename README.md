PlayBootstrap
=============

1- Clone the repo
2- Run SBT (>= 0.13)
3- Run the Play application
=> You have a working web application with a minimal set of common functionnalities that you can start extend



To start development with you personal configuration type in your sbt prompt :
[PlayBoostrap] $ run -Dconfig.resource=personal.conf

To create a default admin :
$ curl -v -X POST http://localhost:9000/admin/default
