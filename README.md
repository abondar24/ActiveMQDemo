# JMS Demo

Set of JMS demos. As a broker used Apache ActiveMQ

## Demos

#### P2P
Point to Point Demos using message queue.

- Jobs - sent job names with ids to corresponding queues and consume them. (job)
- Qborrower - loan request using  queue(qbr)
- Qlender - accept or decline loan request using  queue(ql)

Note: Qborrower and Qlender work together

#### Brokers

- Security - small broker with authentication(disable running broker). (sec)
- Kaha - broker with KahaDB(disable running broker). (kaha)

## Build and run

```yaml
mvn clean install

java -jar <jar-location>/jmsDemo.jar <demo-name>
```
Demo args are listed in description in brackets 
