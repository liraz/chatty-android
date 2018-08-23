/**
 * Copyright Liraz Shilkrot
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.lirazs.chatty.util;

import com.scottyab.aescrypt.AESCrypt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mac on 3/17/17.
 */

public class SecurityUtils {
    //AESCrypt-ObjC uses SHA-256 (and so a 256-bit key)
    private static final String HASH_ALGORITHM = "SHA-256";

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public static String generatePassword(String groupId) {
        return "0123456789";
    }

    public static String encryptText(String text, String groupId) {
        String encryptedMsg = null;

        try {
            String password = generatePassword(groupId);
            encryptedMsg = AESCrypt.encrypt(password, text);

        } catch (GeneralSecurityException e) {
            //handle error
        }
        return encryptedMsg;
    }


    public static String decryptText(String text, String groupId) {
        String messageAfterDecrypt = null;

        try {
            String password = generatePassword(groupId);
            messageAfterDecrypt = AESCrypt.decrypt(password, text);
        } catch (GeneralSecurityException e) {
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }

        return messageAfterDecrypt;
    }

    //TODO: md5HashOfData
    /*//-------------------------------------------------------------------------------------------------------------------------------------------------
    + (NSString *)md5HashOfData:(NSData *)data
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        if (data != nil)
        {
            unsigned char digest[CC_MD5_DIGEST_LENGTH];
            NSMutableString *output = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];

            CC_MD5(data.bytes, (CC_LONG)data.length, digest);

            for (int i=0; i<CC_MD5_DIGEST_LENGTH; i++)
            {
			[output appendFormat:@"%02x", digest[i]];
            }
            return output;
        }
        return nil;
    }*/
    public static String md5OfBytes(byte[] data) {
        //TODO: md5HashOfData
        return null;
    }

    //TODO: md5HashOfPath  -md
    /*//-------------------------------------------------------------------------------------------------------------------------------------------------
    + (NSString *)md5HashOfPath:(NSString *)path
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        if ([[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:nil])
        {
            NSData *data = [NSData dataWithContentsOfFile:path];
            return [self md5HashOfData:data];
        }
        return nil;
    }*/


    public static String md5OfString(String str) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes(Charset.forName("US-ASCII")), 0, str.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //TODO: shaHashOfData
    /*//-------------------------------------------------------------------------------------------------------------------------------------------------
    + (NSString *)shaHashOfData:(NSData *)data
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        if (data != nil)
        {
            unsigned char digest[CC_SHA1_DIGEST_LENGTH];
            NSMutableString *output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];

            CC_SHA1(data.bytes, (CC_LONG)data.length, digest);

            for (int i=0; i<CC_SHA1_DIGEST_LENGTH; i++)
            {
			[output appendFormat:@"%02x", digest[i]];
            }
            return output;
        }
        return nil;
    }*/

    //TODO: shaHashOfPath
    /*//-------------------------------------------------------------------------------------------------------------------------------------------------
    + (NSString *)shaHashOfPath:(NSString *)path
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        if ([[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:nil])
        {
            NSData *data = [NSData dataWithContentsOfFile:path];
            return [self shaHashOfData:data];
        }
        return nil;
    }*/

    //TODO: shaHashOfString
    /*//-------------------------------------------------------------------------------------------------------------------------------------------------
    + (NSString *)shaHashOfString:(NSString *)string
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        if (string != nil)
        {
            NSData *data = [string dataUsingEncoding:NSUTF8StringEncoding];
            return [self shaHashOfData:data];
        }
        return nil;
    }*/


    public static byte[] encryptBytes(byte[] data, String groupId) {
        byte[] encryptedData = null;

        try {
            String password = generatePassword(groupId);
            final SecretKeySpec key = generateKey(password);

            encryptedData = AESCrypt.encrypt(key, ivBytes, data);

        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            //handle error
        }
        return encryptedData;
    }


    public static byte[] decryptBytes(byte[] data, String groupId) {
        byte[] decryptedData = null;

        try {
            String password = generatePassword(groupId);
            final SecretKeySpec key = generateKey(password);

            decryptedData = AESCrypt.decrypt(key, ivBytes, data);

        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            //handle error
        }
        return decryptedData;
    }

    //TODO: encryptFile
    /*+ (void)encryptFile:(NSString *)path groupId:(NSString *)groupId
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        NSData *dataDecrypted = [NSData dataWithContentsOfFile:path];
        NSData *dataEncrypted = [self encryptData:dataDecrypted groupId:groupId];
	[dataEncrypted writeToFile:path atomically:NO];
    }*/

    //TODO: decryptFile
    /*+ (void)decryptFile:(NSString *)path groupId:(NSString *)groupId
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        NSData *dataEncrypted = [NSData dataWithContentsOfFile:path];
        NSData *dataDecrypted = [self decryptData:dataEncrypted groupId:groupId];
	[dataDecrypted writeToFile:path atomically:NO];
    }*/

    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    private static SecretKeySpec generateKey(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

}
