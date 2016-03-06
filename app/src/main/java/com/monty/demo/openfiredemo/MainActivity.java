package com.monty.demo.openfiredemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smack.packet.Presence;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText edtLoginID;
    private EditText edtPwd;
    private Button btnConnection;
    private Button btnRegister;
    private Button btnLogin;

    String loginID = "";
    String password = "";

    private XMPPTCPConnection xmpptcpConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtLoginID = (EditText) findViewById(R.id.edtLoginID);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        btnConnection = (Button) findViewById(R.id.btnConnection);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnConnection.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);


    }

    /**
     * 创建XMPPTCP连接对象
     * @return
     */
    private XMPPTCPConnection getXmpptcpConnection(){
        String server = "192.168.1.103";
        int port = 5222;
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setHost(server);
        builder.setPort(port);
        builder.setServiceName("Monty-PC");  // 只能使用域名/PC机器名称
        builder.setCompressionEnabled(false);
        builder.setDebuggerEnabled(true);
        builder.setSendPresence(true);

        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
        return connection;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnConnection:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        xmpptcpConnection = getXmpptcpConnection();
                        try {
                            xmpptcpConnection.connect();
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.btnLogin:
                loginID = edtLoginID.getText().toString();
                password = edtPwd.getText().toString();

                if(TextUtils.isEmpty(loginID) || TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"账号或密码为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if(xmpptcpConnection.isConnected()){
                                xmpptcpConnection.disconnect(); // 注销连接
                            }
                            xmpptcpConnection.connect();  // 重新连接

                            xmpptcpConnection.login(loginID, password);  //登录

                            Presence presence = new Presence(Presence.Type.available);
                            presence.setStatus("我在线");
                            xmpptcpConnection.sendStanza(presence);

                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.btnRegister:
                loginID = edtLoginID.getText().toString();
                password = edtPwd.getText().toString();

                if(TextUtils.isEmpty(loginID) || TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"账号或密码为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if(xmpptcpConnection.isConnected()){
                                xmpptcpConnection.disconnect(); // 注销连接
                            }
                            xmpptcpConnection.connect();  // 重新连接

                            AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
                            if(accountManager.supportsAccountCreation()){  // 是否支持创建账户
                                Map<String,String> map = new Hashtable<String, String>();
                                map.put("email","monty@xmpp.com");
                                map.put("android","creatUser-android");
                                accountManager.createAccount(loginID,password,map); // 创建账户
                            }
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }

    }
}
