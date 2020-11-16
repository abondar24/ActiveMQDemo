# JMS Demo

Set of JMS demos. As a broker used Apache ActiveMQ

## Demos

#### P2P
Point to Point Demos using message queue.

- Job Consumer - consume job names with ids (jc)
- Job Publisher - send job names with ids to corresponding queues (jp)
- Queue borrower - loan request using  queue(qbr)
- Queue lender - accept or decline loan request using  queue(ql)
- Loan Queue Browser - another consumer for loan request queue which reads loan statuses (qb)
- Metadata - read broker metadata (md)

1. Note: Queue borrower, Queue Lender and Loan Queue Browser work together
2. Note: Job Consumer and Job Publisher work together

#### PubSub
Publisher Subscribe Demos using message topics

- Chat - topic based chat. Better to run multiple instances (chat)
- Portfolio Consumer - read stock data from topic (pfc)
- Portfolio Publisher - publish stock data to topic (pfp)
- Topic borrower - change rate using topic(tbr)
- Topic lender - update rate based on the one from topic(tl)
- Sync Server - embedded active mq server with topic (ss)
- Sync Client - client embedded active mq server with topic (sc)

1. Note: Portfolio Consumer and Portfolio Publisher work together
2. Note: Topic Borrower and Topic Lender work together
3. Note: Sync Server and Sync Client work together


#### Brokers

- Security - small broker with authentication(disable running broker). (sec)
- Kaha - broker with KahaDB(disable running broker). (kaha)

## Build and run

```yaml
mvn clean install

java -jar <jar-location>/jmsDemo.jar <demo-name>
```
Demo args are listed in description in brackets 
