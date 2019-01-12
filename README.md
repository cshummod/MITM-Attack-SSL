#	Man In The Middle Attacks and Authentication over SSL   v(1.0)

## 1.Introduction 
We developed an SSL Proxy that intercept HTTPS request made by a user using MITM Attack approach. Also, the attacker can access the proxy remotely through MITMAdminClient. The figure below illustrates the system design. 

<img src="./figure 1.jpg" style="display: block; margin-left: auto; margin-right: auto; width: 70%;">


## 2.Design
<h4>2.1 Key Generation</h4>
<p>We have used a popular tool called <i><b>keytool</b></i>  to generate and manage keys and certificates. our key store is named "myKeyStore" with password "keystore". The private key was signed using RSA algorithm.
	
	keytool -genkey -keyalg RSA -keystore myKeyStore
</p>



<h4>2.2 MITM Proxy Server</h4>
<p>After the user make SSL Connect request from his browser. The proxy will contact the server and request its certificate then we use the DN and serial number of the original certificate from the server but changes its key into our public key, where the certificate is signed by our private key using SHA256 with RSA then send it back to the client browser. The browser will notify the user to add an exception to accept the certificate. So its all about forging the server certificate.</p>

<h4>2.3 Password Generation</h4>
<p>The password was salted and hashed by BCrypt <i><b>hashpw</b></i> function then stored inside "hashedPassFile".</p> 


## 3.Requirements
		* Any version of Windows/Mac or Linux OS(Recomended and tested)
		* Java installed inside Linux bash	
		* Make installed inside Linux bash
		* Firefox


## 4.Usage
		1. bash ./setup.bash
		2. make
		3. CLASSPATH=".:iaik_jce.jar" java mitm.MITMProxyServer -keyStore myKeyStore
		   -keyStorePassword 'keystore'  -outputFile output.txt
		4. Change Firefox network settings to "localhost:8001" (Default) or whatever port number
		showed in the bash window

####
		To run MITMAdminClient

		1. open new bash window to the same directory 
		2. run the following command:
			java mitm.MITMAdminClient -userName mohammed -userPassword admin -cmd [shutdown|stats]


## 5.Key Stores
| Name          | Password      |
| ------------- |:-------------:|
| myKeyStore    | keystore      |



## 6.Proxy Log
Attached with the project.

## 7.Short Answers
<i>Q1) Why is this attack less successful on Firefox 3 (and later)?</i></br>
A more security added to extended validation SSL certificate information. A new SSL error pages that is clearer and stricter to encounters an invalid SSL certificate.

<i>Q2) What are the advantages and disadvantages of their approach?</i></br>
+ Protection provided to protect you from being vulnerable to an eavesdropper.</br>
- Prevent you from accessing to a real outdated website. An outdated website means that the TLS version no longer secure and need to be updated.  

<i>Q3) How else might you change a web browser to make it less likely that a user would be fooled by an attack like the one you implemented?</i></br>
We simply disable the ability for the user to add an exception in the browser or we can use public key pinning technology to prevent such attacks. The idea is the certificate chain must include a whitelisted public key. 


## 8.Contributors
Mohammed Mahdi Ibrahim

Abdulrahman Mohammed Bin Saif

Saleh Mohammed Alajlan

## 9.Support
For any related questions about the program you can contact me at wmm@hotmail.it




