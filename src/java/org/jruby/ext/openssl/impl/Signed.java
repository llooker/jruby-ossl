/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2008 Ola Bini <ola.bini@gmail.com>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.ext.openssl.impl;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/** PKCS7_SIGNED
 *
 * @author <a href="mailto:ola.bini@gmail.com">Ola Bini</a>
 */
public class Signed {
    /**
     * Describe version here.
     */
    private int version;

    /**
     * Describe crl here.
     */
    private List<X509CRL> crl = new ArrayList<X509CRL>();

    /**
     * Describe cert here.
     */
    private List<X509Certificate> cert = new ArrayList<X509Certificate>();

    /**
     * Describe mdAlgs here.
     */
    private Set<AlgorithmIdentifier> mdAlgs = new HashSet<AlgorithmIdentifier>();

    /**
     * Describe signerInfo here.
     */
    private Set<SignerInfo> signerInfo = new HashSet<SignerInfo>();

    PKCS7 contents;

    /**
     * Get the <code>Version</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getVersion() {
        return version;
    }

    /**
     * Set the <code>Version</code> value.
     *
     * @param newVersion The new Version value.
     */
    public final void setVersion(final int newVersion) {
        this.version = newVersion;
    }

    /**
     * Get the <code>SignerInfo</code> value.
     *
     * @return a <code>Set<SignerInfo></code> value
     */
    public final Set<SignerInfo> getSignerInfo() {
        return signerInfo;
    }

    /**
     * Set the <code>SignerInfo</code> value.
     *
     * @param newSignerInfo The new SignerInfo value.
     */
    public final void setSignerInfo(final Set<SignerInfo> newSignerInfo) {
        this.signerInfo = newSignerInfo;
    }

    /**
     * Get the <code>MdAlgs</code> value.
     *
     * @return a <code>Set<AlgorithmIdentifier></code> value
     */
    public final Set<AlgorithmIdentifier> getMdAlgs() {
        return mdAlgs;
    }

    /**
     * Set the <code>MdAlgs</code> value.
     *
     * @param newMdAlgs The new MdAlgs value.
     */
    public final void setMdAlgs(final Set<AlgorithmIdentifier> newMdAlgs) {
        this.mdAlgs = newMdAlgs;
    }

    /**
     * Get the <code>Contents</code> value.
     *
     * @return a <code>PKCS7</code> value
     */
    public final PKCS7 getContents() {
        return contents;
    }

    /**
     * Set the <code>Contents</code> value.
     *
     * @param newContents The new Contents value.
     */
    public final void setContents(final PKCS7 newContents) {
        this.contents = newContents;
    }

    /**
     * Get the <code>Cert</code> value.
     *
     * @return a <code>List<X509Certificate></code> value
     */
    public final List<X509Certificate> getCert() {
        return cert;
    }

    /**
     * Set the <code>Cert</code> value.
     *
     * @param newCert The new Cert value.
     */
    public final void setCert(final List<X509Certificate> newCert) {
        this.cert = newCert;
    }

    /**
     * Get the <code>Crl</code> value.
     *
     * @return a <code>List<X509CRL></code> value
     */
    public final List<X509CRL> getCrl() {
        return crl;
    }

    /**
     * Set the <code>Crl</code> value.
     *
     * @param newCrl The new Crl value.
     */
    public final void setCrl(final List<X509CRL> newCrl) {
        this.crl = newCrl;
    }

    @Override
    public String toString() {
        return "#<Signed version=" + version + " mdAlgs="+mdAlgs+" content="+contents+" cert="+cert+" crls="+crl+" signerInfos="+signerInfo+">";
    }

    /**
     * SignedData ::= SEQUENCE {
     *   version Version,
     *   digestAlgorithms DigestAlgorithmIdentifiers,
     *   contentInfo ContentInfo,
     *   certificates [0] IMPLICIT ExtendedCertificatesAndCertificates OPTIONAL,
     *   crls [1] IMPLICIT CertificateRevocationLists OPTIONAL,
     *   signerInfos SignerInfos }
     *
     * Version ::= INTEGER
     *
     * DigestAlgorithmIdentifiers ::= SET OF DigestAlgorithmIdentifier
     *
     * SignerInfos ::= SET OF SignerInfo
     */
    public static Signed fromASN1(DEREncodable content) {
        ASN1Sequence sequence = (ASN1Sequence)content;
        DERInteger version = (DERInteger)sequence.getObjectAt(0);
        ASN1Set digestAlgos = (ASN1Set)sequence.getObjectAt(1);
        DEREncodable contentInfo = sequence.getObjectAt(2);

        DEREncodable certificates = null;
        DEREncodable crls = null;

        int index = 3;
        DEREncodable tmp = sequence.getObjectAt(index);
        if((tmp instanceof DERTaggedObject) && ((DERTaggedObject)tmp).getTagNo() == 0) {
            certificates = ((DERTaggedObject)tmp).getObject();
            index++;
        }

        tmp = sequence.getObjectAt(index);
        if((tmp instanceof DERTaggedObject) && ((DERTaggedObject)tmp).getTagNo() == 1) {
            crls = ((DERTaggedObject)tmp).getObject();
            index++;
        }

        ASN1Set signerInfos = (ASN1Set)sequence.getObjectAt(index);

        Signed signed = new Signed();
        signed.setVersion(version.getValue().intValue());
        signed.setMdAlgs(algorithmIdentifiersFromASN1Set(digestAlgos));
        signed.setContents(PKCS7.fromASN1(contentInfo));
        if(certificates != null) {
            System.err.println("Certs: " + certificates);
        }
        if(crls != null) {
            System.err.println("CRLs: " + crls);
        }
        signed.setSignerInfo(signerInfosFromASN1Set(signerInfos));

        return signed;
    }

    private static Set<AlgorithmIdentifier> algorithmIdentifiersFromASN1Set(DEREncodable content) {
        ASN1Set set = (ASN1Set)content;
        Set<AlgorithmIdentifier> result = new HashSet<AlgorithmIdentifier>();
        for(Enumeration<?> e = set.getObjects(); e.hasMoreElements();) {
            result.add(AlgorithmIdentifier.getInstance(e.nextElement()));
        }
        return result;
    }

    private static Set<SignerInfo> signerInfosFromASN1Set(DEREncodable content) {
        ASN1Set set = (ASN1Set)content;
        Set<SignerInfo> result = new HashSet<SignerInfo>();
        for(Enumeration<?> e = set.getObjects(); e.hasMoreElements();) {
            result.add(SignerInfo.getInstance(e.nextElement()));
        }
        return result;
    }
}// Signed
