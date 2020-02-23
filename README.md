IP2Location Java Package
======================

This Java package provides a fast lookup of country, region, city, latitude, longitude, ZIP code, time zone, ISP, domain name, connection type, IDD code, area code, weather station code, station name, mcc, mnc, mobile brand, elevation, and usage type from IP address by using IP2Location database. This package uses a file based database available at IP2Location.com. This database simply contains IP blocks as keys, and other information such as country, region, city, latitude, longitude, ZIP code, time zone, ISP, domain name, connection type, IDD code, area code, weather station code, station name, mcc, mnc, mobile brand, elevation, and usage type as values. It supports both IP address in **IPv4** and **IPv6**.

Maven Dependency
======================

```
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>28.2-jre</version>
</dependency>
```

Example
=======

```java
import com.lbanyan.sdk.ip2location.IP2Location;
import com.lbanyan.sdk.ip2location.IP2LocationResult;

public class Application {
    public static void main(String[] args) {
        try {
            IP2Location ip2Location = new IP2Location("IPV6-COUNTRY-REGION-CITY-LATITUDE-LONGITUDE-ZIPCODE-TIMEZONE-ISP-DOMAIN.BIN");
            IP2LocationResult result = ip2Location.query("8.8.8.8");

            System.out.println(result.country_code);
            System.out.println(result.country_name);
            System.out.println(result.city);
            System.out.println(result.isp);
            
            result = ip2Location.query("2001:4860:4860::8888");

            System.out.println(result.country_code);
            System.out.println(result.country_name);
            System.out.println(result.city);
            System.out.println(result.isp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

IPv4 BIN vs IPv6 BIN
====================

Use the IPv4 BIN file if you just need to query IPv4 addresses.
Use the IPv6 BIN file if you need to query BOTH IPv4 and IPv6 addresses.

Database
========

Free LITE databases are available at https://lite.ip2location.com upon registration.

The paid databases are available at https://ip2location.com under subscription package.

Thanks
========
https://github.com/stdfox/ip2location-java
