package servletToVerifyTest;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

/** verifica la validità di un certificato tramite il protocollo OCSP (bozza) */
public class OCSPVerify{

  /** certificato da verificare */
  private X509Certificate certificate;
  /** numero seriale del certificato */
  private BigInteger serialNumber;

  /** costruttore */
  public OCSPVerify(X509Certificate certificate){

    this.certificate = certificate;
    this.serialNumber = certificate.getSerialNumber();
  }
  /**abilito la verifica tramite OCSP*/
  static{
    Security.setProperty("ocsp.enable", "true");
  }


  /**
   * genera una request
   * 
   * */
  // TODO Terminare javadoc
  public OCSPReq generateOCSPRequest(){

    if((this.serialNumber == null) || (this.certificate == null)){
      System.err.println("OCSPverify.generateOCSPRequest() " + "parametro null!");
      System.exit(1);
    }

    X509CertificateHolder certificateHolder = null;
    try{
      certificateHolder = new X509CertificateHolder(this.certificate.getEncoded());
    }
    catch(CertificateEncodingException |IOException e){
          
      System.err.println("OCSPVerify.generateOCSPRequest() " + "certificato Holder non creato!");
      e.printStackTrace();
      System.exit(1);
    }

    JcaDigestCalculatorProviderBuilder providerBuilder = new JcaDigestCalculatorProviderBuilder().setProvider("BC");
    if(providerBuilder == null){
      System.err.println("OCSPverify.generateOCSPRequest() " + "providerBuilder is null!");
      System.exit(1);
    }

    DigestCalculatorProvider digestCalculatorProv = null;
    try{
      digestCalculatorProv = providerBuilder.build();
    }
    catch(OperatorCreationException e){
      System.err.println("OCSPVerify.generateOCSPRequest() " + "digestCalculatorProv non creato!");
      e.printStackTrace();
      System.exit(1);
    }

    if(digestCalculatorProv == null){
      System.err.println("OCSPverify2.generateOCSPRequest() " + "digestCalculator is null!");
      System.exit(1);
    }

    DigestCalculator digestCal = null;
    try{
      digestCal = digestCalculatorProv.get(CertificateID.HASH_SHA1);
    }
    catch(OperatorCreationException e){
      System.err.println("OCSPVerify.generateOCSPRequest() " + "digestCal non creato!");
      e.printStackTrace();
      System.exit(1);
    }

    if(digestCal == null){
      System.err.println("OCSPverify2.generateOCSPRequest() " + "digestCal is null!");
      System.exit(1);
    }

    CertificateID id = null;
    try{
      id = new CertificateID(digestCal, certificateHolder, serialNumber);
    }
    catch(OCSPException e){
      System.err.println("OCSPVerify.generateOCSPRequest() " + "id certificate non creato!");
      e.printStackTrace();
      System.exit(1);
    }
    if(id == null){
      System.err.println("OCSPVerify.generateOCSPRequest() " + "id is null!");
      System.exit(1);
    }

    /** request usando il nonce */
    OCSPReqBuilder ocspGen = new OCSPReqBuilder();

    // aggiungo l'ID
    ocspGen.addRequest(id);

    // creo il nonce per evitare gli attacchi di tipo replay
    BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());

    Extension ext =
        new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, true, new DEROctetString(nonce.toByteArray()));

    ocspGen.setRequestExtensions(new Extensions(new Extension[] { ext }));

    // la request non è firmata
    try{
      return ocspGen.build();
    }
    catch(OCSPException e){
      System.err.println("OCSPVerify.generateOCSPRequest() " + "ocspGen non creato!");
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  /** genera una response */
  // TODO: terminare javadoc
  public OCSPResp getOCSPResponce(String serviceUrl, OCSPReq request){

    if((request == null) || (serviceUrl == null)){
      System.err.println("OCSPverify2.getOCSPResponce() " + "parametro null!");
      System.exit(1);
    }

    try{

      byte[] array = request.getEncoded();
      if(serviceUrl.startsWith("http")){

        HttpURLConnection con;
        URL url = new URL(serviceUrl);

        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/ocsp-request");
        con.setRequestProperty("Accept", "application/ocsp-response");
        con.setDoOutput(true); 
        OutputStream out = con.getOutputStream();

        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));

        dataOut.write(array);
        dataOut.flush();
        dataOut.close();

        // Get Response
        InputStream in = (InputStream) con.getContent();
        OCSPResp ocspResponse = new OCSPResp(in);
        if(OCSPResponseStatus.SUCCESSFUL==ocspResponse.getStatus()) {
          System.out.println("Il server ha risposto...");
        }
     // Looking for errors in the response:
        if (con.getResponseCode() / 100 != 2) {
          System.out.println("OCSPVerify.getOCSPResponce() "+"errore nella response!");
          System.err.println("OCSPVerify.getOCSPResponce() "+"response non conforme!");
          System.exit(1);
        }

        return ocspResponse;
        
      }else{
        System.err.println(("indirizzo URL errato: " + "deve iniziare con http!"));
        System.exit(1);
      }
    }
    catch(IOException e){
      System.out.println("Impossibile ottenere una response da " + serviceUrl);
      System.err.println(("Cannot get ocspResponse from url: " + serviceUrl));
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  /** restituisce lo stato di un certificato X509 tramite il protocollo OCSP */
  // TODO: terminare javadoc
  public String analyseResponse(OCSPResp response, OCSPReq request, X509Certificate caCert) throws Exception{

    if((caCert == null) || (request == null) || (response == null)){
      System.err.println("OCSPVerify.analyseResponse() " + "parametro null!");
      System.exit(1);
    }

    /* Analyse the response send regarding the request the certificate that signed the response etc .. */
    BasicOCSPResp basicResponse = (BasicOCSPResp) response.getResponseObject(); // retrieve the Basic Resp of the
                                                                                // Response
    if(basicResponse == null){
      System.err.println("OCSPVerify.analyseResponse() " + "basicResponse is null!");
      System.exit(1);
    }

    // verify the response
    if(basicResponse.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(
        caCert.getPublicKey()))){
      
      SingleResp[] responses = basicResponse.getResponses();
      System.out.println("numero di response singole: "+responses.length);

      byte[] reqNonce = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnId().getEncoded();
      byte[] respNonce = basicResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnId().getEncoded();

      // validate the nonce if it is present
      if(reqNonce == null || Arrays.equals(reqNonce, respNonce)){ // If both nonce are equals

        String message = "";
         for (int i = 0; i < responses.length;) { 
           
          message += " certificate number " + responses[i].getCertID().getSerialNumber();
          
          if(responses[i].getCertStatus() == CertificateStatus.GOOD){
            
            return message + " status: good";
          }else{
            
            return message + " status: revoked";
          }
        }
        
        return message;
      }else {
        
      
        return "response nonce failed to validate";
      }
   }else {
      return "response failed to verify OCSP signature";
     }
  }

  /** restituisce l'URL del certificato per l'OCSP */
  // TODO terminare javadoc e metodo
  public String getOcspUrl(X509Certificate certificate){

    @SuppressWarnings("deprecation")
    byte[] octetBytes = certificate.getExtensionValue(X509Extension.authorityInfoAccess.getId());

    DLSequence dlSequence = null;
    ASN1Encodable asn1Encodable = null;

    try{
      ASN1Primitive fromExtensionValue = X509ExtensionUtil.fromExtensionValue(octetBytes);
      if(!(fromExtensionValue instanceof DLSequence))
        return null;
      dlSequence = (DLSequence) fromExtensionValue;
      for (int i = 0; i < dlSequence.size(); i++){
        asn1Encodable = dlSequence.getObjectAt(i);
        if(asn1Encodable instanceof DLSequence)
          break;
      }
      if(!(asn1Encodable instanceof DLSequence))
        return null;
      dlSequence = (DLSequence) asn1Encodable;
      for (int i = 0; i < dlSequence.size(); i++){
        asn1Encodable = dlSequence.getObjectAt(i);
        if(asn1Encodable instanceof DERTaggedObject)
          break;
      }
      if(!(asn1Encodable instanceof DERTaggedObject))
        return null;
      DERTaggedObject derTaggedObject = (DERTaggedObject) asn1Encodable;
      byte[] encoded = derTaggedObject.getEncoded();
      if(derTaggedObject.getTagNo() == 6){
        int len = encoded[1];
        return new String(encoded, 2, len);
      }
    }
    catch(IOException e){
      e.printStackTrace();
    }
    return null;
  }

  
  

  /** bozza di lavoro per l'analisi dell'OCSP */
  public String analyseResponse2(OCSPResp response, OCSPReq request, X509Certificate caCert) throws Exception{

    if((caCert == null) || (request == null) || (response == null)){
      System.err.println("OCSPVerify.analyseResponse() " + "parametro null!");
      System.exit(1);
    }

    /* Analyse the response send regarding the request the certificate that signed the response etc .. */
    BasicOCSPResp basicResponse = (BasicOCSPResp) response.getResponseObject(); // retrieve the Basic Resp of the
                                                                                // Response
    if(basicResponse == null){
      System.err.println("OCSPVerify.analyseResponse() " + "basicResponse is null!");
      System.exit(1);
    }

    X509CertificateHolder hold[] = basicResponse.getCerts();
    for (int i = 0; i < hold.length; i++){
      System.out.println("certificato hold " + i + " " + hold[i]);
    }
    byte[] sign = basicResponse.getSignature();
    if(sign == null){
      System.out.println("sign is null!");
      System.exit(1);
    }

    JcaContentVerifierProviderBuilder bul = new JcaContentVerifierProviderBuilder();
    JcaContentVerifierProviderBuilder bul2 = bul.setProvider("BC");
    if(bul2 == null){
      System.out.println("bul2 is null!");
      System.exit(1);
    }

    PublicKey pubK = caCert.getPublicKey();
    if(pubK == null){
      System.out.println("pubK is null!");
      System.exit(1);
    }

    ContentVerifierProvider verCert = bul2.build(pubK);
    if(verCert == null){
      System.out.println("verCert is null!");
      System.exit(1);
    }
    // verify the response
    if(checkSignature(verCert, sign, hold[0])){
      
      SingleResp[] responses = basicResponse.getResponses();

      byte[] reqNonce = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnId().getEncoded();
      byte[] respNonce = basicResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnId().getEncoded();

      // validate the nonce if it is present
      if(reqNonce == null || Arrays.equals(reqNonce, respNonce)){ // If both nonce are equals

        String message = "";
        for (int i = 0; i != responses.length;){
          message += " certificate number " + responses[i].getCertID().getSerialNumber();
          if(responses[i].getCertStatus() == null) {
            System.out.println("cert status null!! riga 418");
          }else {
            CertificateStatus status = responses[i].getCertStatus();
            System.out.println("status: "+status);
          }
          if(responses[i].getCertStatus() == CertificateStatus.GOOD){
            return message + " status: good";
          }else{
            return message + " status: revoked";
          }
        }
        return message;
      }else
        return "response nonce failed to validate";
    }else
      return "response failed to verify OCSP signature";
  }

}
