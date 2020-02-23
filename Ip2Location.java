package com.lbanyan.sdk.ip2location;

import com.google.common.net.InetAddresses;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;

public class Ip2Location {

    private class Metadata {

        int databasetype;
        int databasecolumn;
        int databaseyear;
        int databasemonth;
        int databaseday;
        long ipv4databasecount;
        long ipv4databaseaddr;
        long ipv6databasecount;
        long ipv6databaseaddr;
        long ipv4indexbaseaddr;
        long ipv6indexbaseaddr;
        long ipv4columnsize;
        long ipv6columnsize;
    }

    private static final int[] COUNTRY_POSITION = {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
    private static final int[] REGION_POSITION = {0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
    private static final int[] CITY_POSITION = {0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
    private static final int[] ISP_POSITION = {0, 0, 3, 0, 5, 0, 7, 5, 7, 0, 8, 0, 9, 0, 9, 0, 9, 0, 9, 7, 9, 0, 9, 7, 9};
    private static final int[] LATITUDE_POSITION = {0, 0, 0, 0, 0, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
    private static final int[] LONGITUDE_POSITION = {0, 0, 0, 0, 0, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
    private static final int[] DOMAIN_POSITION = {0, 0, 0, 0, 0, 0, 0, 6, 8, 0, 9, 0, 10, 0, 10, 0, 10, 0, 10, 8, 10, 0, 10, 8, 10};
    private static final int[] ZIPCODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 7, 0, 7, 7, 7, 0, 7, 0, 7, 7, 7, 0, 7};
    private static final int[] TIMEZONE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 7, 8, 8, 8, 7, 8, 0, 8, 8, 8, 0, 8};
    private static final int[] NETSPEED_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 11, 0, 11, 8, 11, 0, 11, 0, 11, 0, 11};
    private static final int[] IDDCODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 12, 0, 12, 0, 12, 9, 12, 0, 12};
    private static final int[] AREACODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 13, 0, 13, 0, 13, 10, 13, 0, 13};
    private static final int[] WEATHERSTATIONCODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 14, 0, 14, 0, 14, 0, 14};
    private static final int[] WEATHERSTATIONNAME_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 15, 0, 15, 0, 15, 0, 15};
    private static final int[] MCC_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 16, 0, 16, 9, 16};
    private static final int[] MNC_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 17, 0, 17, 10, 17};
    private static final int[] MOBILEBRAND_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 18, 0, 18, 11, 18};
    private static final int[] ELEVATION_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 19, 0, 19};
    private static final int[] USAGETYPE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 20};

    private static final BigInteger MAX_IPV4_RANGE = BigInteger.valueOf(4294967295L);
    private static final BigInteger MAX_IPV6_RANGE = new BigInteger("340282366920938463463374607431768211455");

    private RandomAccessFile fileHandle;
    private Metadata metadata;

    public Ip2Location(String file) throws IOException {

        fileHandle = new RandomAccessFile(file, "r");

        metadata = new Metadata();
        metadata.databasetype = read8(1);
        metadata.databasecolumn = read8(2);
        metadata.databaseyear = read8(3);
        metadata.databasemonth = read8(4);
        metadata.databaseday = read8(5);
        metadata.ipv4databasecount = read32(6);
        metadata.ipv4databaseaddr = read32(10);
        metadata.ipv6databasecount = read32(14);
        metadata.ipv6databaseaddr = read32(18);
        metadata.ipv4indexbaseaddr = read32(22);
        metadata.ipv6indexbaseaddr = read32(26);
        metadata.ipv4columnsize = (long) metadata.databasecolumn << 2;
        metadata.ipv6columnsize = (long) (16 + ((metadata.databasecolumn - 1) << 2));
    }

    private int read8(long position) throws IOException {

        fileHandle.seek(position - 1);

        return fileHandle.read();
    }

    private long read32(long position) throws IOException {

        fileHandle.seek(position - 1);

        long[] data = new long[4];
        for (int i = 0; i < data.length; i++) {
            data[i] = fileHandle.read();
        }

        return data[3] << 24 | data[2] << 16 | data[1] << 8 | data[0];
    }

    private BigInteger read128(long position) throws IOException {

        fileHandle.seek(position - 1);

        long[] data = new long[16];
        for (int i = 0; i < data.length; i++) {
            data[i] = fileHandle.read();
        }

        long lo1 = data[3] << 24 | data[2] << 16 | data[1] << 8 | data[0];
        long lo2 = data[7] << 24 | data[6] << 16 | data[5] << 8 | data[4];
        long lo3 = data[11] << 24 | data[10] << 16 | data[9] << 8 | data[8];
        long lo4 = data[15] << 24 | data[14] << 16 | data[13] << 8 | data[12];
        return BigInteger.valueOf(lo4).shiftLeft(96).or(BigInteger.valueOf(lo3).shiftLeft(64))
                .or(BigInteger.valueOf(lo2).shiftLeft(32)).or(BigInteger.valueOf(lo1));
    }

    private float readFloat(long position) throws IOException {

        fileHandle.seek(position - 1);

        int[] data = new int[4];
        for (int i = 0; i < data.length; i++) {
            data[i] = fileHandle.read();
        }

        return Float.intBitsToFloat(data[3] << 24 | data[2] << 16 | data[1] << 8 | data[0]);
    }

    private String readString(long position) throws IOException {

        fileHandle.seek(position);

        int i = fileHandle.read();
        char[] data;
        try {
            data = new char[i];
            for (int j = 0; j < i; j++) {
                data[j] = ((char) fileHandle.read());
            }
        } catch (NegativeArraySizeException e) {
            return null;
        }

        return String.copyValueOf(data);
    }

    public Ip2LocationResult query(String ip) throws IOException {

        Ip2LocationResult result = new Ip2LocationResult(ip);

        if (ip == null || ip.length() == 0) {
            return result;
        }

        long low = 0;
        long high;
        long mid;
        BigInteger ipfrom;
        BigInteger ipto;
        BigInteger ipno;
        BigInteger maxip;
        int firstcol;
        int iptype;

        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid ip address", e);
        }

        if (address instanceof Inet6Address) {
            iptype = 6;
            firstcol = 16;
            high = metadata.ipv6databasecount;
            maxip = MAX_IPV6_RANGE;
        } else {
            iptype = 4;
            firstcol = 4;
            high = metadata.ipv4databasecount;
            maxip = MAX_IPV4_RANGE;
        }
        ipno = InetAddresses.toBigInteger(address);
        if (ipno.compareTo(maxip) == 0) {
            ipno = ipno.add(BigInteger.valueOf(-1L));
        }

        while (low <= high) {
            mid = (low + high) >> 1;
            long rowOffset;
            if (iptype == 6) {
                rowOffset = metadata.ipv6databaseaddr + mid * metadata.ipv6columnsize;
                ipfrom = read128(rowOffset);
                ipto = read128(rowOffset + metadata.ipv6columnsize);
            } else {
                rowOffset = metadata.ipv4databaseaddr + mid * metadata.ipv4columnsize;
                ipfrom = BigInteger.valueOf(read32(rowOffset));
                ipto = BigInteger.valueOf(read32(rowOffset + metadata.ipv4columnsize));
            }

            if (ipno.compareTo(ipfrom) > -1 && ipno.compareTo(ipto) == -1) {
                long offset;

                if (COUNTRY_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (COUNTRY_POSITION[metadata.databasetype] - 2));
                    result.country_code = readString(offset);

                    offset += 3;
                    result.country_name = readString(offset);
                }

                if (REGION_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (REGION_POSITION[metadata.databasetype] - 2));
                    result.region = readString(offset);
                }

                if (CITY_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (CITY_POSITION[metadata.databasetype] - 2));
                    result.city = readString(offset);
                }

                if (ISP_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (ISP_POSITION[metadata.databasetype] - 2));
                    result.isp = readString(offset);
                }

                if (LATITUDE_POSITION[metadata.databasetype] != 0) {
                    offset = rowOffset + firstcol + 4 * (LATITUDE_POSITION[metadata.databasetype] - 2);
                    result.latitude = readFloat(offset);
                }

                if (LONGITUDE_POSITION[metadata.databasetype] != 0) {
                    offset = rowOffset + firstcol + 4 * (LONGITUDE_POSITION[metadata.databasetype] - 2);
                    result.longitude = readFloat(offset);
                }

                if (DOMAIN_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (DOMAIN_POSITION[metadata.databasetype] - 2));
                    result.domain = readString(offset);
                }

                if (ZIPCODE_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (ZIPCODE_POSITION[metadata.databasetype] - 2));
                    result.zipcode = readString(offset);
                }

                if (TIMEZONE_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (TIMEZONE_POSITION[metadata.databasetype] - 2));
                    result.timezone = readString(offset);
                }

                if (NETSPEED_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (NETSPEED_POSITION[metadata.databasetype] - 2));
                    result.netspeed = readString(offset);
                }

                if (IDDCODE_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (IDDCODE_POSITION[metadata.databasetype] - 2));
                    result.iddcode = readString(offset);
                }

                if (AREACODE_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (AREACODE_POSITION[metadata.databasetype] - 2));
                    result.areacode = readString(offset);
                }

                if (WEATHERSTATIONCODE_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (WEATHERSTATIONCODE_POSITION[metadata.databasetype] - 2));
                    result.weatherstation_code = readString(offset);
                }

                if (WEATHERSTATIONNAME_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (WEATHERSTATIONNAME_POSITION[metadata.databasetype] - 2));
                    result.weatherstation_name = readString(offset);
                }

                if (MCC_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (MCC_POSITION[metadata.databasetype] - 2));
                    result.mcc = readString(offset);
                }

                if (MNC_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (MNC_POSITION[metadata.databasetype] - 2));
                    result.mnc = readString(offset);
                }

                if (MOBILEBRAND_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (MOBILEBRAND_POSITION[metadata.databasetype] - 2));
                    result.mobilebrand = readString(offset);
                }

                if (ELEVATION_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (ELEVATION_POSITION[metadata.databasetype] - 2));
                    String data = readString(offset);

                    if (data != null) {
                        result.elevation = Float.parseFloat(data);
                    }
                }

                if (USAGETYPE_POSITION[metadata.databasetype] != 0) {
                    offset = read32(rowOffset + firstcol + 4 * (USAGETYPE_POSITION[metadata.databasetype] - 2));
                    result.usagetype = readString(offset);
                }

                break;
            }

            if (ipno.compareTo(ipfrom) == -1) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return result;
    }
}
