/*
 * Copyright (c) 2017. Cogative LTD.
 */

import com.enverido.java.api.ApiClient;
import com.enverido.java.licences.Licence;
import com.enverido.java.products.Product;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class Verification {

    public static void main(String[] args) {
        // Setup an API client to query the Enverido server with. The parameter must be the organisation name
        // associated with your Enverido account.
        ApiClient api = new ApiClient("cogative");

        // We need the product's public key to verify the signature returned by the Enverido licence server.
        // For the Java library, we need to use the PKCS #1 public key, WITHOUT the --- BEGIN ... --- and --- END ... ---
        // header/footer. If this isn't entered correctly we won't be able to verify licences.
        //
        // The product's public key is available on its page within the Enverido Web UI
        String productPublicKey = "MIGJAoGBAN/cgylbD9A7R2fbuyaYuMisX0I9QfLvKGyhfIrPpOrkMeN3PlQGDYGSTBWZmpaCH2lwWZco1+ziNZwqtJxmjrNIF6UZu44fjx7d7KgIFNNXuzO2j2OlqNmfoJSIE+uFNYncrdQWSzoaR2ELzkpfZlLlfgviIMPxYPXq0DN3lYBRAgMBAAE=";

        // Setup a scanner to get some input from the user.
        Scanner input = new Scanner(System.in);

        // Output some information to the user, including some example licences to use.
        System.out.println("======================================================================================");
        System.out.println("Welcome to the Enverido Java Example Application. Example licence details are below...");
        System.out.println("======================================================================================");
        System.out.println("Licence #1");
        System.out.println("----------");
        System.out.println("Shortcode: ENVJAVA-3716810427");
        System.out.println("Email: example@enverido.com");
        System.out.println("Hardware identifier (A dummy MAC address): ed:d2:65:0d:91:13");
        System.out.println("======================================================================================");

        // Fetch some information about the licence from the user. We could do this in a number of ways. Here we
        // just fetch console input, but you could use Swing or JavaFX (or any library) if you have a GUI application.
        System.out.print("Please enter your licence shortcode for our example Java product: ");
        String licenceShortcode = input.nextLine();

        System.out.print("Please enter the licence holder's email address: ");
        String licenceEmail = input.nextLine();

        // Normally you would get a hardware identifier directly from the machine (for example, directly retrieving
        // the machine's MAC address), but for the purposes of the example we'll ask the user to enter an identifier.
        System.out.print("Please enter the licence's hardware identifier: ");
        String licenceHIdentifier = input.nextLine();

        try {
            // Lookup the licence to verify using the lookup(...) method. This will retrieve some more information
            // from the Enverido licence server (product ID, licence ID) that we can use to verify the information
            // that the user gave us.
            Licence licenceToVerify = Licence.lookup(licenceShortcode, api);

            // Add the user-entered information to the licence we just looked up. If your product locked licences
            // to IP addresses instead of hardware identifiers, you would use the setIp(...) method. Same logic applies
            // for domain names. A product can lock to more than one piece of information. For example it may lock
            // to both an IP address and a hardware identifier and both must be correct for the licence to be valid.
            licenceToVerify.setEmail(licenceEmail);
            licenceToVerify.setHardwareIdentifier(licenceHIdentifier);

            // Print out a message as it might take a second or two to verify the licence with the Enverido server
            System.out.println("Verifying licence... Please wait...");

            // Echo whether or not the licence was valid.
            boolean valid = Licence.verify(licenceToVerify, api, "thisisarandomtoken", productPublicKey);
            System.out.println("Licence is valid? " + valid);

        } catch(Exception ex) {
            // An exception is typically thrown if there was a problem with the user input. For example
            // if a shortcode that doesn't exist is entered.
            System.err.println("An error occured! Did you enter the correct licence shortcode?");
        }

    }

}
