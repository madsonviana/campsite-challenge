## Upgrade Campsite Challenge

This project exposes a REST api that manages a campsite reservations.

_This is an implementation of an Upgrade's challenge._

### Technologies

 * Spring Boot
 * Spring Webflux
 * JMS (Java Message Service)
 * MongoDB
 * ActiveMQ

### How to use

To start the development server on ```8080``` port you can use the following command.

```
mvn spring-boot:run
```

The ```com.upgrade.campsite.MultipleRequestTest``` class test multiple parallel requests to creation of reservation on the same dates.


### Notes

* The reactive paradigm (Spring Webflux) was choose to provide a REST api that can handle large volume of requests.  

* The JMS was choose as message system on functionality of creating reservation to assurance no errors of overlapping on reservations. 