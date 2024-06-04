package com.example.educash;
import android.widget.EditText;

import java.math.BigInteger;

public class Hashing_Algorithm {
    public static BigInteger hashPassword(String password_to_hash) {

        BigInteger hash = BigInteger.ZERO;
        BigInteger primeMultiplier = new BigInteger("1000003");

        for (int i = 0; i < password_to_hash.length(); ++i) {
            hash = hash.multiply(primeMultiplier).add(BigInteger.valueOf(password_to_hash.charAt(i)));
            hash = hash.and(BigInteger.valueOf(0xFFFFFFFF)).xor(hash.shiftRight(32));
            hash = hash.multiply(new BigInteger("1000007"));
            System.out.println(hash); //Hash output
        }
        return hash;
    }
}

