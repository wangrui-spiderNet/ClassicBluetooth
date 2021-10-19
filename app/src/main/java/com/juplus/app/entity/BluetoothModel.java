package com.juplus.app.entity;



public class BluetoothModel {

    private static volatile BluetoothModel instance = null;

    private BluetoothModel() {
        //do something
    }

    /**
     * 这个模式将同步内容下方到if内部，提高了执行的效率，不必每次获取对象时都进行同步，只有第一次才同步，创建了以后就没必要了。
     * 这种模式中双重判断加同步的方式，比传统懒汉式的效率大大提升，因为如果单层if判断，在服务器允许的情况下，
     * 假设有一百个线程，耗费的时间为100*（同步判断时间+if判断时间），而如果双重if判断，100的线程可以同时if判断，理论消耗的时间只有一个if判断的时间。
     * 所以如果面对高并发的情况，而且采用的是懒汉模式，最好的选择就是双重判断加同步的方式。
     */
    public static BluetoothModel getInstance() {
        if (instance == null) {
            synchronized (BluetoothModel.class) {
                if (instance == null) {
                    instance = new BluetoothModel();
                }
            }
        }
        return instance;
    }

    private String productNumber    = "";
    private String dh               = "";
    private String softwareVersion  = "";
    private String bluetoothName    = "";
    private String bluetoothAddress = "";
    private String softwareDate     = "--";
    private String producer         = "--";

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getSoftwareDate() {
        return softwareDate;
    }

    public void setSoftwareDate(String softwareDate) {
        this.softwareDate = softwareDate;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void clear() {
        productNumber = "";
        dh = "";
        softwareVersion = "";
        bluetoothName = "";
        bluetoothAddress = "";
        softwareDate = "--";
        producer = "--";
    }
}
