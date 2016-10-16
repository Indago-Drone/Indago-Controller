package com.lewei.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WifiFunction {
    public static final String DEVICE_HEAD = "lw6301-";
    private static boolean isThreadRunNow;
    private static WifiManager meWifiManager;
    private Thread connThread;
    private Context context;
    private boolean isStop;
    private ConnectivityManager meConnectivityManager;
    private List<WifiConfiguration> meWifiConfigurations;
    private WifiInfo meWifiInfo;
    private List<ScanResult> meWifiList;
    WifiLock meWifiLock;

    /* renamed from: com.lewei.lib.WifiFunction.1 */
    class C00441 extends Thread {
        C00441() {
        }

        public void run() {
            if (WifiFunction.isThreadRunNow) {
                Log.d("WiFiFunction", "thread has been already running....");
                return;
            }
            WifiFunction.isThreadRunNow = true;
            WifiFunction.this.isStop = false;
            while (!WifiFunction.this.isStop) {
                if (!WifiFunction.meWifiManager.isWifiEnabled()) {
                    WifiFunction.meWifiManager.setWifiEnabled(true);
                }
                String currName = WifiFunction.this.getCurrSSID();
                if (currName != null && currName.toLowerCase(Locale.getDefault()).contains(WifiFunction.DEVICE_HEAD)) {
                    break;
                }
                String devName = WifiFunction.this.getDeviceNeedConn();
                Log.e("Connect", "what want SSID:" + devName + "  pid:" + Thread.currentThread().getId());
                if (devName != null && devName.length() > 0) {
                    if (WifiFunction.this.isDeviceConnected()) {
                        currName = WifiFunction.this.getCurrSSID();
                        if (currName != null) {
                            if (currName.toLowerCase(Locale.getDefault()).contains(WifiFunction.DEVICE_HEAD)) {
                                break;
                            }
                            WifiFunction.this.connDevice(devName);
                        } else {
                            continue;
                        }
                    } else {
                        WifiFunction.this.connDevice(devName);
                    }
                }
                WifiFunction.this.Sleep(1000);
            }
            WifiFunction.isThreadRunNow = false;
        }
    }

    static {
        isThreadRunNow = false;
    }

    public WifiFunction(Context context) {
        this.isStop = false;
        this.context = context;
        meWifiManager = (WifiManager) context.getSystemService("wifi");
        this.meWifiInfo = meWifiManager.getConnectionInfo();
        this.meConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
    }

    public void openWifi() {
        if (!meWifiManager.isWifiEnabled()) {
            meWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (!meWifiManager.isWifiEnabled()) {
            meWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return meWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        this.meWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (this.meWifiLock.isHeld()) {
            this.meWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        this.meWifiLock = meWifiManager.createWifiLock("test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.meWifiConfigurations;
    }

    public void connetionConfiguration(int index) {
        if (index <= this.meWifiConfigurations.size()) {
            meWifiManager.enableNetwork(((WifiConfiguration) this.meWifiConfigurations.get(index)).networkId, true);
        }
    }

    public void startScan() {
        meWifiManager.startScan();
        this.meWifiList = meWifiManager.getScanResults();
        this.meWifiConfigurations = meWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getWifiList() {
        return this.meWifiList;
    }

    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.meWifiList.size(); i++) {
            sb.append("Index_" + Integer.valueOf(i + 1).toString() + ":");
            sb.append(((ScanResult) this.meWifiList.get(i)).toString()).append("\n~");
        }
        return sb;
    }

    public String getMacAddress() {
        return this.meWifiInfo == null ? "NULL" : this.meWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return this.meWifiInfo == null ? "NULL" : this.meWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return this.meWifiInfo == null ? 0 : this.meWifiInfo.getIpAddress();
    }

    public int getNetWordId() {
        return this.meWifiInfo == null ? 0 : this.meWifiInfo.getNetworkId();
    }

    public String getWifiInfo() {
        return this.meWifiInfo == null ? "NULL" : this.meWifiInfo.toString();
    }

    public void addNetWork(WifiConfiguration configuration) {
        meWifiManager.enableNetwork(meWifiManager.addNetwork(configuration), true);
    }

    public void disConnectionWifi(int netId) {
        meWifiManager.disableNetwork(netId);
        meWifiManager.disconnect();
    }

    public int getCurrRssiLevel() {
        this.meWifiInfo = meWifiManager.getConnectionInfo();
        return this.meWifiInfo == null ? 0 : WifiManager.calculateSignalLevel(this.meWifiInfo.getRssi(), 5);
    }

    public String getCurrSSID() {
        this.meWifiInfo = meWifiManager.getConnectionInfo();
        return this.meWifiInfo == null ? null : this.meWifiInfo.getSSID();
    }

    public void stopThread() {
        this.isStop = true;
    }

    public void startConnThread() {
        if (this.connThread == null || !this.connThread.isAlive()) {
            this.connThread = new C00441();
            this.connThread.start();
        }
    }

    private String getDeviceNeedConn() {
        if (this.meConnectivityManager.getNetworkInfo(1) == null) {
            return null;
        }
        List<ScanResult> list = new ArrayList();
        meWifiManager.startScan();
        list = meWifiManager.getScanResults();
        if (list.size() <= 0) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            ScanResult scanResult = (ScanResult) list.get(i);
            if (scanResult.SSID.toLowerCase(Locale.getDefault()).startsWith(DEVICE_HEAD)) {
                return scanResult.SSID;
            }
        }
        return null;
    }

    private boolean isDeviceConnected() {
        State state = this.meConnectivityManager.getNetworkInfo(1).getState();
        if (state == State.CONNECTED) {
            return true;
        }
        if (state == State.DISCONNECTED) {
            return false;
        }
        if (state == State.CONNECTING) {
            return false;
        }
        return false;
    }

    private void connDevice(String ssid) {
        WifiFunction mWifiFunction = new WifiFunction(this.context);
        int lastIndex = (((int) (Math.random() * 100.0d)) + ((int) (Math.random() * 100.0d))) + 30;
        if (lastIndex == 123) {
            lastIndex++;
        }
        String ipString = "172.16.55." + lastIndex;
        mWifiFunction.setStatic(ssid, ipString, "172.16.55.1", "8.8.8.8");
        WifiConfiguration wifiConfig = mWifiFunction.CreateWifiInfo(ssid, "", 1, ipString, "172.16.55.1", "8.8.8.8");
        if (wifiConfig != null) {
            mWifiFunction.addNetWork(wifiConfig);
            Log.e("Connect", "connecting " + ssid + " now.....");
        }
    }

    private void Sleep(int ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type, String static_ip, String static_gateway, String static_dns) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == 1) {
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) {
            config.hiddenSSID = true;
            config.wepKeys[0] = Password;
            setStatic(SSID, static_ip, static_gateway, static_dns);
            config.allowedAuthAlgorithms.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedGroupCiphers.set(2);
            config.allowedGroupCiphers.set(0);
            config.allowedGroupCiphers.set(1);
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
            config.status = 2;
            meWifiManager.updateNetwork(config);
        }
        if (Type == 3) {
            setStatic(SSID, static_ip, static_gateway, static_dns);
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(2);
            config.allowedKeyManagement.set(1);
            config.allowedPairwiseCiphers.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedPairwiseCiphers.set(2);
            config.status = 2;
        }
        return config;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == 1) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) {
            config.hiddenSSID = true;
            config.wepKeys[0] = Password;
            try {
                setIpAssignment("STATIC", config);
                setProxy("NONE", config);
                setIpAddress(InetAddress.getByName("172.16.0.10"), 16, config);
                setGateway(InetAddress.getByName("172.16.0.1"), config);
                setDNS(InetAddress.getByName("8.8.8.8"), config);
            } catch (Exception e) {
                Log.e("ken", "set static fail");
            }
            config.allowedAuthAlgorithms.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedGroupCiphers.set(2);
            config.allowedGroupCiphers.set(0);
            config.allowedGroupCiphers.set(1);
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
            config.status = 2;
            meWifiManager.updateNetwork(config);
        }
        if (Type == 3) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(2);
            config.allowedKeyManagement.set(1);
            config.allowedPairwiseCiphers.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedPairwiseCiphers.set(2);
            config.status = 2;
        }
        return config;
    }

    public static WifiConfiguration IsExsits(String SSID) {
        for (WifiConfiguration existingConfig : meWifiManager.getConfiguredNetworks()) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setProxy(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "proxySettings");
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties != null) {
            Object linkAddress = Class.forName("android.net.LinkAddress").getConstructor(new Class[]{InetAddress.class, Integer.TYPE}).newInstance(new Object[]{addr, Integer.valueOf(prefixLength)});
            ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
            mLinkAddresses.clear();
            mLinkAddresses.add(linkAddress);
        }
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties != null) {
            Object routeInfo = Class.forName("android.net.RouteInfo").getConstructor(new Class[]{InetAddress.class}).newInstance(new Object[]{gateway});
            ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
            mRoutes.clear();
            mRoutes.add(routeInfo);
        }
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties != null) {
            ArrayList<InetAddress> mDnses = (ArrayList) getDeclaredField(linkProperties, "mDnses");
            mDnses.clear();
            mDnses.add(dns);
        }
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        return obj.getClass().getField(name).get(obj);
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value)); //findme (Class<Enum>) added
    }

    public void setStatic(String SSID, String static_ip, String static_gateway, String static_dns) {
        WifiConfiguration tempConfig = IsExsits(SSID);
        if (tempConfig != null) {
            try {
                setIpAssignment("STATIC", tempConfig);
                setIpAddress(InetAddress.getByName(static_ip), 16, tempConfig);
                setGateway(InetAddress.getByName(static_gateway), tempConfig);
                setDNS(InetAddress.getByName(static_dns), tempConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            meWifiManager.updateNetwork(tempConfig);
        }
    }
}
