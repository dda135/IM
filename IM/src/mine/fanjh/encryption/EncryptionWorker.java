package mine.fanjh.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mine.fanjh.utils.TextUtils;
import sun.misc.BASE64Encoder;

public class EncryptionWorker {
	private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIwhVR9dE7dRs+p5CqtxU6vjLBts\n"
			+ "8HOPkuWo4Dy15OKSf55oMlyQ/ehUKF/rOx/NMLjvUHQSj3cw9fSVUrcLLFdk6k+Jak5niQyaAjU3\n"
			+ "gX5D0EZUMRwSGdWndeG6Yy3Z2c2O3h5zelO2iEpyVSl2vYPN/zyFVADZxBIiLfN/bns7AgMBAAEC\n"
			+ "gYBRl9cIvBmO1HP+QxyDVylxHIXCMlyP7TmLoBlxQDhV9Rd6FRG99G7jqJ0ZvM5gZgnIpRAjhesj\n"
			+ "a87K62eOTWMzYshHATuKDLvSymsFYY63bt8d8r8cxq34wg9YnrKmhoMy6gE+5iP80t7SuAy7X8Oz\n"
			+ "ALZ/JyQD6zw2yyU3UmoIAQJBAMx2xourwtiyC5Ov4vhSGVR9htIxPX07c3dhTDWGL3axQtF0GJll\n"
			+ "srCqRFq8YZiGdrl4YZyDLhzroYXsT/cEuUECQQCvc1y0TvId/RytCAuIyotFQbPnIZOe/l+4Bc6i\n"
			+ "vB0GPwUEHyYcZVOxovNejPNc/KQTEVJI3I5eYza0wZGOajl7AkBulQq6/bGLK3hxbt5NuXFzrdRe\n"
			+ "GD2OXroLZfcmt6UyB5sA1056oHMtc1k2zc3nBUpu8zmvwY8OGy6n1PBGxCpBAkEAjmgqlMeHScQK\n"
			+ "JH/lLNCJnlsn9LCSK3j4pFtCT2A0hr9cCO5ndqDf/8ztkI8DcTQ20Ks8iJtMi1woKSr8RAYARQJB\n"
			+ "AMDCdAz2elK6X3bw0jSbKOmtsxmx6KJ8gHKuXG51wgEqFpGcftAzPusvnI1Ih+5hhDnsch2rrrU9\n" + "ntLjNxpxAgQ=";

	public static final String[] deciphering(String content) {
		if (TextUtils.isTextEmpty(content)) {
			return null;
		}
		if (content.length() > 20) {
			String contentWithKey = content.substring(10, content.length() - 10);
			String desContent = contentWithKey.substring(0, contentWithKey.length() - 172);
			String desKeyWithRsa = contentWithKey.substring(contentWithKey.length() - 172, contentWithKey.length());
			String desKey = SecUtil.decrypt(privateKey, desKeyWithRsa);
			try {
				String originalContent = DesUtil.decrypt(desContent, desKey);
				return new String[] { desKey, originalContent };
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static final String encrypt(String B, String content) {
		String transformContent = "";
		String D = GetKey.getKey(8);
		String headContent = GetKey.getKey(10);// 内容头部的10个字符串
		String tailContent = GetKey.getKey(10);// 内容尾部的10个字符串
		try {
			String DWithDes = DesUtil.encrypt(D, B);
			String contentWithDes = DesUtil.encrypt(content, D);
			transformContent = headContent + DWithDes + "|" + contentWithDes + tailContent;
			return transformContent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String MD5(String content) {
		if(TextUtils.isTextEmpty(content)) {
			return "";
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BASE64Encoder base64en = new BASE64Encoder();
		String newstr = content;
		try {
			if(null != md5) {
				newstr = base64en.encode(md5.digest(content.getBytes("utf-8")));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newstr = content;
		}
		return newstr;

	}

}
