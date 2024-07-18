package com.hkgov.csb.localSigning.util;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.cms.*;
import org.bouncycastle.cms.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wraps a InputStream into a CMSProcessable object for bouncy castle. It's a memory saving
 * alternative to the {@link org.bouncycastle.cms.CMSProcessableByteArray CMSProcessableByteArray}
 * class.
 *
 * @author Thomas Chojecki
 */
class CMSProcessableInputStream implements CMSTypedData
{
    private final InputStream in;
    private final ASN1ObjectIdentifier contentType;

    CMSProcessableInputStream(InputStream is)
    {
        this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), is);
    }

    CMSProcessableInputStream(ASN1ObjectIdentifier type, InputStream is)
    {
        contentType = type;
        in = is;
    }

    @Override
    public Object getContent()
    {
        return in;
    }

    @Override
    public void write(OutputStream out) throws IOException, CMSException
    {
        // read the content only one time
        in.transferTo(out);
        in.close();
    }

    @Override
    public ASN1ObjectIdentifier getContentType()
    {
        return contentType;
    }
}
