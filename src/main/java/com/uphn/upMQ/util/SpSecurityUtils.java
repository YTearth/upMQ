package com.uphn.upMQ.util;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import org.apache.log4j.Logger;

public class SpSecurityUtils {
	
	private static Logger logger = Logger.getLogger(SpSecurityUtils.class);
	
	/**
	 * 
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/**
	 * 
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * 
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * 
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * 
	 * @param bitsize
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genKeyPair(int bitsize) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(bitsize);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		LogUtil.writeLog("=====开始签名=====");
		byte[] keyBytes = MyBase64Utils.decode(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		try {
			signature.initSign(privateK);
			LogUtil.writeLog("=====签名初始化结束=====");
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.toString());
		}
		signature.update(data);
		return new String(MyBase64Utils.encode(signature.sign()));
	}

	public static String signSafe(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = MyBase64Utils.decode(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		try {
			signature.initSign(privateK);
		} catch (Exception e) {
			logger.warn("{}", e);
		}
		signature.update(data);
		return new String(MyBase64Utils.encodeSafe(signature.sign()));
	}

	/**
	 * 
	 * @param data
	 * @param publicKey
	 * @param sign
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = MyBase64Utils.decode(publicKey.getBytes());
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(MyBase64Utils.decode(sign.getBytes()));
	}

	/**
	 * 
	 * @param encryptedData
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
		byte[] keyBytes = MyBase64Utils.decode(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		return decrypt(encryptedData, privateK, null);
	}

	public static byte[] decryptByPrivateKey(byte[] data, String modulus, String exponent, Provider provider)
			throws Exception {
		return decrypt(data, getPrivateKey(modulus, exponent), provider);
	}

	/**
	 * 
	 * @param encryptedData
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
		byte[] keyBytes = MyBase64Utils.decode(publicKey.getBytes());
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		return decrypt(encryptedData, publicK, null);
	}

	public static byte[] decryptByPublicKey(byte[] data, String modulus, String exponent, Provider provider)
			throws Exception {
		return decrypt(data, getPublicKey(modulus, exponent), provider);
	}

	/**
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
		byte[] keyBytes = MyBase64Utils.decode(publicKey.getBytes());
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return encrypt(data, keyFactory.generatePublic(x509KeySpec), null);
	}

	public static byte[] encryptByPublicKey(byte[] data, String modulus, String exponent, Provider provider)
			throws Exception {
		return encrypt(data, getPublicKey(modulus, exponent), provider);
	}
	

	/**
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = MyBase64Utils.decode(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return encrypt(data, keyFactory.generatePrivate(pkcs8KeySpec), null);
	}

	public static byte[] encryptByPrivateKey(byte[] data, String modulus, String exponent, Provider provider)
			throws Exception {
		return encrypt(data, getPrivateKey(modulus, exponent), provider);
	}

	/**
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return new String(MyBase64Utils.encode(key.getEncoded()));
	}

	/**
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return new String(MyBase64Utils.encode(key.getEncoded()));
	}

	//----------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param modulus
	 * @param pubicExponent
	 * @return
	 */
	public static RSAPublicKey getPublicKey(String modulus, String pubicExponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(pubicExponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			logger.warn("{}", e);
			return null;
		}
	}


	/**
	 * 
	 * @param modulus
	 * @param privateExponent
	 * @return
	 * @throws Exception
	 */
	public static RSAPrivateKey getPrivateKey(String modulus, String privateExponent) throws Exception {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(privateExponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			logger.warn("{}", e);
			return null;
		}
	}

	public static byte[] encrypt(byte[] data, Key key, Provider provider) throws Exception {
		Cipher cipher = null;
		ByteArrayOutputStream out = null;
		try {

			
			if (provider == null) cipher = Cipher.getInstance(KEY_ALGORITHM);
			else
				cipher = Cipher.getInstance(KEY_ALGORITHM, provider);
			cipher.init(Cipher.ENCRYPT_MODE, key);

			int blockSize = cipher.getBlockSize();
			if (blockSize <= 0) blockSize = MAX_ENCRYPT_BLOCK;
			logger.info("blockSize:" + blockSize);

			//int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
			int inputLen = data.length;
			out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > blockSize) {
					cache = cipher.doFinal(data, offSet, blockSize);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * blockSize;
			}
			byte[] encryptedData = out.toByteArray();
			return encryptedData;
		} finally {
			cipher = null;
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
				out = null;
			}
			provider = null;
		}
	}

	public static byte[] decrypt(byte[] encryptedData, Key key, Provider provider) throws Exception {
		Cipher cipher = null;
		ByteArrayOutputStream out = null;
		try {

			if (provider == null) cipher = Cipher.getInstance(KEY_ALGORITHM);
			else
				cipher = Cipher.getInstance(KEY_ALGORITHM, provider);

			cipher.init(Cipher.DECRYPT_MODE, key);

			int blockSize = cipher.getBlockSize();
			if (blockSize <= 0) blockSize = MAX_DECRYPT_BLOCK;
			logger.info("blockSize:" + blockSize);

			int inputLen = encryptedData.length;
			out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > blockSize) {
					cache = cipher.doFinal(encryptedData, offSet, blockSize);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * blockSize;
			}
			byte[] decryptedData = out.toByteArray();

			return decryptedData;
		} finally {
			cipher = null;
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
				out = null;
			}
			provider = null;
		}
	}

	public static void main(String[] args) {
		String data = "{\"amount\":\"1500\",\"mchntCd\":\"027430187654321\",\"payeeComments\":\"测试\",\"qrValidCount\":\"1\",\"qrValidTime\":\"60\"}";		
		System.out.println(data);
		String pubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtFGOpRgGkjaQPFfRvqEBismsjzBppiTvXs7hvYBaBup54iVoPYYbFeE3SRLK3zOxIzJ8qradABZqUIOIxchYbTPjSoaR7wLDgb9pk1kmfB71HmofG9puySPTvpTVkCMacLZEehbpO4xx2j19Gv/p+xlBqBVIseNzTp82cjkpz7QIDAQAB";
		String prikey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK0UY6lGAaSNpA8V9G+oQGKyayPMGmmJO9ezuG9gFoG6nniJWg9hhsV4TdJEsrfM7EjMnyqtp0AFmpQg4jFyFhtM+NKhpHvAsOBv2mTWSZ8HvUeah8b2m7JI9O+lNWQIxpwtkR6Fuk7jHHaPX0a/+n7GUGoFUix43NOnzZyOSnPtAgMBAAECgYBcQ5o9Ckyl47upLxL20sI/2syycIND7xwviGaxOI/G6CzCJLYVrO+jJNaXWHfM8ziiNjJDFf8qadJVVJI/uYl+cy91TyoiC2O5CFkSsNSgl5S/E/5Uc0wrv151NX6om8SLzeJF4dESu7ivuvUmSAZYhmmH9cGsLt/vjRNxTQRVAQJBAOJHJcRLATJn3j5hhueaLVrGICEAdxFTKPmLfbUx1kWBcsOU+Ibq9W/DVh3WRzA9sY7linsfpOBKLkwrtNGQM/kCQQDD0GOoERKFVRxeAk/g6g4bBHiZ9xmZqDavxPpwIdD73FmGQS1LBomaMGsKvGZ2fxG1ylImaWKaERqfb4CtYtSVAkBGdzOaqmToBpKeSI7TZx8CqrpsrJFn0sbq13bBS5DXulU79RNkKJ1gPat+xTEMI9o8jt0ONK+KrW83h1DbBhY5AkEAsCtCHakOcqq6FNIbr4ykGCaTomGvxJCUctrTPiMOdCow2Rq2dzNwhSpeg5Aw1xdHhbh65FgX/+i3fQ3CRTwPaQJATndht+4F+iiZtnamuHshoutV4lHGxVxaG0T0ycDrZMiHJKvRFDlBPq2XhhBMt8pqFZcrGNfQ63xhkk5PJFgerw==";
		String sign = "JSK1uPT44S+n2usFId/qWo99CKBKt+8gEVvshGZ3hw+ZKGPP1iB3GdnGTyQy71k0l6RS3kszX78F/GP/lYigAcaUhbWwjPE/ObQeEF+Mf4rGAcbHSlAMHIuf/4Evfc25mXW+CpSndJjakqYZGBQPJrcMSbuywAZswk1grWBddMA=";
		try {
			String sign1 = SpSecurityUtils.sign(data.getBytes("utf-8"), prikey);
			System.out.println(sign1);
			System.out.println(SpSecurityUtils.verify(data.getBytes("utf-8"), pubkey, sign1));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
