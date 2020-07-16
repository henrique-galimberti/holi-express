FROM java:8-jre

ENV MICROSERVICE_HOME /opt/microservice

EXPOSE 9000

COPY build/libs/api-gateway-0.0.1-SNAPSHOT.jar $MICROSERVICE_HOME/
COPY conf/docker.json $MICROSERVICE_HOME/

WORKDIR $MICROSERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -Dfile.encoding=UTF8 -jar api-gateway-0.0.1-SNAPSHOT.jar run com.ilegra.holiexpress.gateway.APIGatewayVerticle -conf docker.json"]