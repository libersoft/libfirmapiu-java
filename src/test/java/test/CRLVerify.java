import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

/** verifica la CRL di un certificato di tipo X509 */
@SuppressWarnings("deprecation")
public class CRLVerify{

  /** certificato di tipo X509 */
  private X509Certificate certificate;
  /** CRL del certificato */
  private X509CRL cRLCertificate;
  /** punti di distribuzione delle CRL */
  private List<String> crlDistPoints;

  /**
   * costruttore della classe CRLVerify
   * 
   * @param certificate
   *          certificato di tipo X509
   */
  public CRLVerify(X509Certificate certificate){

    this.certificate = certificate;

    if(this.certificate == null){
      System.err.println("CRLVerify.CRLVerify() " + "certificato null!");
      System.exit(1);
    }
  }

  /** restituisce il certificato di tipo X509
   * @return certificato di tipo X509: {@code X509Certificate}
   *  */
  public X509Certificate getCertificate(){

    if(this.certificate == null){
      System.err.println("CRLVerify.getCertificate() " + "certificato null!");
      System.exit(1);
    }
    return this.certificate;
  }

  /** restituisce la CRL del certificato
   * @return CRL del certificato: {@code X509CRL} */
  public X509CRL getCRL(){

    this.verifyCertificateCRLs();

    if(this.cRLCertificate == null){

      System.err.println("CRLVerify.getCRL() " + "CRL null!");
      System.exit(1);
    }
    return this.cRLCertificate;
  }

  /** restituisce i punti di distribuzione della CRL 
   * @return punti di distribuzione della CRL: {@code List<String>}*/
  public List<String> getDistrPoint(){

    return this.getCrlDistributionPoints();
  }

  /**
   * verifica la CRL di un certificato
   * 
   * @return <code>true</code> se il certificato non � stato revocato;
   *  <code>false</code> altrimenti
   * */
  public boolean verifyCertificateCRLs(){

    // punti di distibuzione della CRL
    crlDistPoints = getCrlDistributionPoints();

    // verifico se il certificato � stato revocato
    for (String crlDP: crlDistPoints){

      try{
        cRLCertificate = downloadCRL(crlDP);
      }
      catch(CRLException e){
        System.err.println("CRLVerify.verifyCertificateCRLs() " + "errore nella verifica del certificato!");
        e.printStackTrace();
        System.exit(0);
      }
      // certificato revocato
      if(cRLCertificate.isRevoked(this.certificate)){
        return false;
      }
    }
    return true;
  }

  /**
   *restituisce i punti di distribuzione della CRL del certificato
   * 
   * @return punti di distribuzione della CRL del certificato: <code>List<String></code>
   * */
  private List<String> getCrlDistributionPoints(){

    // ottengo il DER-encoded octet string dall'OID passato come argomento
    byte[] crldpExt = this.certificate.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

    if(crldpExt == null){
      List<String> emptyList = new ArrayList<String>();
      return emptyList;
    }

    @SuppressWarnings("resource")
    ASN1InputStream oAsnInStream = new ASN1InputStream(new ByteArrayInputStream(crldpExt));
    ASN1Primitive derObjCrlDP = null;

    try{
      derObjCrlDP = oAsnInStream.readObject();
    }
    catch(IOException e){
      e.printStackTrace();
    }

    DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
    byte[] crldpExtOctets = dosCrlDP.getOctets();

    @SuppressWarnings("resource")
    ASN1InputStream oAsnInStream2 = new ASN1InputStream(new ByteArrayInputStream(crldpExtOctets));
    ASN1Primitive derObj2 = null;
    try{
      derObj2 = oAsnInStream2.readObject();
    }
    catch(IOException e){
      System.err.println("CRLVerify.getCrlDistributionPoints() " + "errore nell'I/O");
      e.printStackTrace();
      System.exit(0);
    }

    CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);

    List<String> crlUrls = new ArrayList<String>();

    for (DistributionPoint dp: distPoint.getDistributionPoints()){

      DistributionPointName dpn = dp.getDistributionPoint();

      if(dpn != null){

        if(dpn.getType() == DistributionPointName.FULL_NAME){

          GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();

          for (int j = 0; j < genNames.length; j++){

            if(genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier){

              String url = DERIA5String.getInstance(genNames[j].getName()).getString();
              crlUrls.add(url);
            }
          }
        }
      }
    }
    return crlUrls;
  }

  /**
   * dato un URL scarica la CRL del certificato
   * 
   * @param crlURL
   *          -URL da dove scaricare la CRL: {@code String}
   * @return CRL del certificato: {@code X509CRL}
   * 
   * */
  private X509CRL downloadCRL(String crlURL) throws CRLException{

    if(crlURL == null){
      System.err.println("CRLVerify.downloadCRL() " + "parametro errato!");
      System.exit(1);
    }

    X509CRL crl = null;
    if(crlURL.startsWith("http://") || crlURL.startsWith("https://") || crlURL.startsWith("ftp://")){

      crl = downloadCRLFromWeb(crlURL);
      return crl;

    }else if(crlURL.startsWith("ldap://")){

      crl = downloadCRLFromLDAP(crlURL);
      return crl;

    }else{
      System.err.println("CRLVerify.downloadCRL() " + "errore nella ricerca della CRL!");
      System.out.println("non � possibile scaricare la CRL dall'URL!!!");
      System.exit(0);
    }
    return crl;
  }

  /**
   * dato un URL di tipo LDAP scarica la CRL del certificato
   * 
   * @param ldapURL
   *          -URL di tipo LDAP: {@code String}
   * @return CRL del certificato: {@code X509CRL}
   * 
   * */
  private X509CRL downloadCRLFromLDAP(String ldapURL){

    if(ldapURL == null){
      System.err.println("CRLVerify.downloadCRLFromLDAP() " + "parametro null!");
      System.exit(1);
    }

    X509CRL crl = null;
    Hashtable<String,String> env = new Hashtable<String,String>();
    // TODO verificare il valore aggiunto (com.sun.jndi.ldap.LdapCtxFactory)
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, ldapURL);

    DirContext ctx = null;
    try{
      ctx = new InitialDirContext(env);
    }
    catch(NamingException e){
      System.err.println("CRLVerify.downloadCRLFromLDAP() " + "errore nella risoluzione del nome!");
      e.printStackTrace();
      System.exit(0);
    }
    Attributes avals = null;
    try{
      avals = ctx.getAttributes("");
    }
    catch(NamingException e){
      System.err.println("CRLVerify.downloadCRLFromLDAP() " + "errore nella risoluzione del nome!");
      e.printStackTrace();
      System.exit(0);
    }
    Attribute aval = avals.get("certificateRevocationList;binary");

    byte[] val = null;
    try{
      val = (byte[]) aval.get();
    }
    catch(NamingException e){
      System.err.println("CRLVerify.downloadCRLFromLDAP() " + "errore nella risoluzione del nome!");
      e.printStackTrace();
      System.exit(0);
    }

    if((val == null) || (val.length == 0)){
      System.out.println("impossibile scaricare da LDAP!!");
      System.err.println("CRLVerify.downloadCRLFromLDAP() " + "errore nel download della CRL da LDAP!");
      System.exit(0);

    }else{

      InputStream inStream = new ByteArrayInputStream(val);
      CertificateFactory cf = null;
      try{
        cf = CertificateFactory.getInstance("X.509");
      }
      catch(CertificateException e){
        System.err.println("CRLVerify.downloadCRLFromLDAP() " + "errore nel certificato!");
        e.printStackTrace();
        System.exit(0);
      }
      try{
        crl = (X509CRL) cf.generateCRL(inStream);
      }
      catch(CRLException e){
        System.err.println("CRLVerify.downloadCRLFromLDAP() " + "errore nella CRL!!");
        e.printStackTrace();
        System.exit(0);
      }

    }
    return crl;
  }

  /**
   * dato un URL scarica una CRL dal Web
   * 
   * @param crlURL
   *          URL dove scaricare la CRL: {@code String}
   * @return CRL del certificato: {@code X509CRL}
   * */
  private X509CRL downloadCRLFromWeb(String crlURL){

    if(crlURL == null){
      System.err.println("CRLVerify.downloadCRLFromWeb() " + "parametro null!");
      System.exit(1);
    }

    URL url = null;
    try{
      url = new URL(crlURL);
    }
    catch(MalformedURLException e){
      System.err.println("CRLVerify.downloadCRLFromWeb() " + "URL non valido!!");
      e.printStackTrace();
      System.exit(0);
    }
    InputStream crlStream = null;
    try{
      crlStream = url.openStream();
    }
    catch(IOException e){
      System.err.println("CRLVerify.downloadCRLFromWeb() " + "errore I/O");
      e.printStackTrace();
      System.exit(0);
    }
    try{
      CertificateFactory cf = null;
      try{
        cf = CertificateFactory.getInstance("X.509");
      }
      catch(CertificateException e){
        System.err.println("CRLVerify.downloadCRLFromWeb() " + "errore nella getInstance!");
        e.printStackTrace();
        System.exit(0);
      }
      X509CRL crl = null;
      try{
        crl = (X509CRL) cf.generateCRL(crlStream);
      }
      catch(CRLException e){
        System.err.println("CRLVerify.downloadCRLFromWeb() " + "errore nella CRL!");
        e.printStackTrace();
        System.exit(0);
      }
      return crl;
    }
    finally{
      try{
        crlStream.close();
      }
      catch(IOException e){
        System.err.println("CRLVerify.downloadCRLFromWeb() " + "stream non chiuso!");
        e.printStackTrace();
      }
    }
  }

  /**
   * dato un set di certificati di tipo X509 restituisce una certificazione di tipo PKIX
   * 
   * @param additionalCerts
   *          insieme di certificati di tipo X509: {@code Set<X509Certificate>}
   * @return il risultato positivo dell'algo per la certificazione di tipo PKIX: {@code PKIXCertPathBuilderResult}
   * *//*
  public PKIXCertPathBuilderResult verifyCertificate(Set<X509Certificate> additionalCerts){

    if(additionalCerts == null){
      System.err.println("CRLVerify.verifyCertificate() "+"parametro null!");
      System.exit(1);
    }
    
    if(isSelfSigned(this.certificate)){
      System.exit(1);
    }

    // serie di CA affidabili
    Set<X509Certificate> trustedRootCerts = new HashSet<X509Certificate>();
    Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();

    for (X509Certificate additionalCert: additionalCerts){
      // costruisco le due serie di CA affidabili, quella intermedia e quella alla radice
      if(isSelfSigned(additionalCert)){
        trustedRootCerts.add(additionalCert);
      }else{
        intermediateCerts.add(additionalCert);
      }
    }

    // provo a creare la catena dei certificati e a verificarla
    PKIXCertPathBuilderResult verifiedCertChain = verifyCertificate(trustedRootCerts, intermediateCerts);
    // verifico se il certificato � stato revocato
    verifyCertificateCRLs();
    // la catena dei certificati � stata realizzata e verificata, restituisco il risultato
    return verifiedCertChain;
  }*/

  /**
   * dato un set di certificati di tipo X509 che rappresentano la radice della
   * CRL, un set di certificati di tipo X509 che rappresentano la catena intermedia della CRL, restituisce il risultato
   * positivo dell'algo per la certificazione di tipo PKIX
   * 
   * @param trustedRootCerts
   *          set di certificati che rappresentano la radice della CRL: {@code Set<X509Certificate>}
   * @param intermediateCerts
   *          set di certificati che rappresentano la catena intermedia della CRL: {@code Set<X509Certificate>}
   * @return il risultato positivo dell'algo per la certificazione di tipo PKIX: {@code PKIXCertPathBuilderResult}
   * *//*
  private PKIXCertPathBuilderResult verifyCertificate(Set<X509Certificate> trustedRootCerts,
      Set<X509Certificate> intermediateCerts){

    if((intermediateCerts == null)||(trustedRootCerts == null)){
      System.err.println("CRLVerify.verifyCertificate() "+"parametro null!");
      System.exit(1);
    }
    
    // creo il selettore che rappresenta il certificato di partenza
    X509CertSelector selector = new X509CertSelector();
    selector.setCertificate(this.certificate);

    // insieme di radici delle CA
    Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();

    for (X509Certificate trustedRootCert: trustedRootCerts){
      trustAnchors.add(new TrustAnchor(trustedRootCert, null));
    }

    // configuro i parametri dell'algo per il costruttore di certificati di tipo PKIX
    PKIXBuilderParameters pkixParams = null;
    try{
      pkixParams = new PKIXBuilderParameters(trustAnchors, selector);
    }
    catch(InvalidAlgorithmParameterException e){
      System.err.println("CRLVerify.verifyCertificate() " + "parametri dell'algo non validi!");
      e.printStackTrace();
      System.exit(1);
    }

    // disabilito il controllo di default delle revoche
    pkixParams.setRevocationEnabled(false);

    // specifico una lista di certificati intermedi
    CertStore intermediateCertStore = null;
    try{
      intermediateCertStore =
          CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediateCerts), "BC");

    }
    catch(InvalidAlgorithmParameterException |
          NoSuchAlgorithmException |
          NoSuchProviderException e){
      System.err.println("CRLVerify.verifyCertificate() "
          + "errore nella creazione della lista di certificati intermedi");
      e.printStackTrace();
      System.exit(1);
    }

    // aggiungo i certificati intermedi
    pkixParams.addCertStore(intermediateCertStore);

    // realizzo e verifico la catena dei certificati
    CertPathBuilder builder = null;
    try{
      builder = CertPathBuilder.getInstance("PKIX", "BC");
    }
    catch(NoSuchAlgorithmException |
          NoSuchProviderException e){
      System.err.println("CRLVerify.verifyCertificate() " + "errore nella getInstance!");
      e.printStackTrace();
      System.exit(1);
    }

    PKIXCertPathBuilderResult result = null;
    try{
      result = (PKIXCertPathBuilderResult) builder.build(pkixParams);
    }
    catch(CertPathBuilderException |
          InvalidAlgorithmParameterException e){
      System.err.println("CRLVerify.verifyCertificate() " + "errore nella builder");
      e.printStackTrace();
      System.exit(1);
    }

    return result;
  }*/

  /**
   * Verifica che il certificato sia stato firmato con la chiave privata corrispondente alla chiave pubblica
   *  
   * @param cert
   *          certificato di tipo X509: {@code X509Certificate}
   * @return {@code true} se il certificato � stato firmato con la chiave privata corrispondente alla chiave pubblica;
   *         {@code false} altrimenti
   * */
  /*private boolean isSelfSigned(X509Certificate cert){

    try{
      // tento di verificare il certificato con la sua chiave pubblica
      PublicKey key = cert.getPublicKey();
      try{
        cert.verify(key);
      }
      catch(CertificateException |
            NoSuchAlgorithmException |
            NoSuchProviderException e){
        System.err.println("CRLVerify.isSelfSigned() " + "errore nella verifica del certificato!");
        e.printStackTrace();
        System.exit(1);
      }
      return true;
    }
    catch(SignatureException sigEx){
      System.err.println("CRLVerify.isSelfSigned() " + "errore nella firma del certificato!");
      return false;
    }
    catch(InvalidKeyException keyEx){
      System.err.println("CRLVerify.isSelfSigned() " + "chiave pubblica invalida!");
      return false;
    }
  }*/

}
