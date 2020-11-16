# JMS Demo

Set of JMS demos. As a broker used Apache ActiveMQ

## Demos

#### P2P
Point to Point Demos using message queue.

- Jobs - sent job names with ids to corresponding queues and consume them. (job)
- Queue borrower - loan request using  queue(qbr)
- Queue lender - accept or decline loan request using  queue(ql)
- Loand Queue Browser - another consumer for loan request queue which reads loan statuses (qb)
- Metadata - read broker metadata (md)
#### PubSub
Publisher Subscribe Demos using message topics

- Chat - topic based chat. Better to run multiple instances (chat)

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
