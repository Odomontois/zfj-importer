package com.thed.jwt;

import com.atlassian.fugue.Option;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.writer.JwtWriterFactory;
import com.thed.util.HttpMethod;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by smangal on 4/25/15.
 */
public abstract class JwtAuthorizationGenerator {
    private static final int JWT_EXPIRY_WINDOW_SECONDS_DEFAULT = 60 * 3;
    private final int jwtExpiryWindowSeconds;

    private final JwtWriterFactory jwtWriterFactory;

    public JwtAuthorizationGenerator(JwtWriterFactory jwtWriterFactory) {
        this(jwtWriterFactory, JWT_EXPIRY_WINDOW_SECONDS_DEFAULT);
    }

    public JwtAuthorizationGenerator(JwtWriterFactory jwtWriterFactory, int jwtExpiryWindowSeconds) {
        this.jwtWriterFactory = checkNotNull(jwtWriterFactory);
        this.jwtExpiryWindowSeconds = jwtExpiryWindowSeconds;
    }

    public abstract Option<String> generate(HttpMethod httpMethod, URI url, Map<String, List<String>> parameters,
                                   Option<String> userId) throws JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException;
}
