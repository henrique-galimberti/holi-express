FROM java:8-jre

ENV MICROSERVICE_HOME /opt/microservice

EXPOSE 9002

COPY build/libs/payment-microservice-0.0.1-SNAPSHOT.jar $MICROSERVICE_HOME/
COPY conf/docker.json $MICROSERVICE_HOME/

WORKDIR $MICROSERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar payment-microservice-0.0.1-SNAPSHOT.jar run com.ilegra.holiexpress.payment.PaymentVerticle -conf docker.json"]