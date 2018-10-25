package Classes;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
    public PublicKey publicKey;
    private PrivateKey privateKey;

    public Wallet(){
        generateKeyPair();
    }

    private void generateKeyPair(){
        try {
            //Elliptic-curve crypto setup
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            //Init & gen
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
