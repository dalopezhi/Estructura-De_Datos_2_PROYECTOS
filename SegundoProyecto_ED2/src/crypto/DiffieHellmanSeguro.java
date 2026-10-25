package crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;

public class DiffieHellmanSeguro {

    public static KeyPair generarClaves()
            throws Exception {

        KeyPairGenerator generador =
                KeyPairGenerator.getInstance("DH");

        generador.initialize(2048);

        return generador.generateKeyPair();
    }

    // El servidor genera claves usando
    // los mismos parametros que el cliente.
    public static KeyPair generarClaves(
            PublicKey claveRemota) throws Exception {

        DHPublicKey claveDH =
                (DHPublicKey) claveRemota;

        DHParameterSpec parametros =
                claveDH.getParams();

        KeyPairGenerator generador =
                KeyPairGenerator.getInstance("DH");

        generador.initialize(parametros);

        return generador.generateKeyPair();
    }

    public static PublicKey reconstruirPublica(
            byte[] datos) throws Exception {

        KeyFactory fabrica =
                KeyFactory.getInstance("DH");

        return fabrica.generatePublic(
                new X509EncodedKeySpec(datos));
    }

    public static byte[] calcularSecreto(
            KeyPair propias,
            PublicKey remota) throws Exception {

        KeyAgreement acuerdo =
                KeyAgreement.getInstance("DH");

        acuerdo.init(propias.getPrivate());
        acuerdo.doPhase(remota, true);

        return acuerdo.generateSecret();
    }
}
