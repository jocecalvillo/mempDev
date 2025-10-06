package com.mx.feenicia.memphis.commom.config.auth;

import com.mx.feenicia.memphis.commom.entity.TblMerchant;
import com.mx.feenicia.memphis.commom.entity.TblMerchantCredential;
import com.mx.feenicia.memphis.commom.repository.TblMerchantCredentialRepository;
import com.mx.feenicia.memphis.commom.repository.TblMerchantRepository;
import com.mx.feenicia.memphis.commom.soter.dictionary.SoterResponseCodes;
import com.mx.feenicia.memphis.commom.soter.dto.request.JwtDecodeRequest;
import com.mx.feenicia.memphis.commom.soter.dto.request.JwtVerifyRequest;
import com.mx.feenicia.memphis.commom.soter.dto.response.JwtDecodeResponse;
import com.mx.feenicia.memphis.commom.soter.dto.response.JwtVerifyResponse;
import com.mx.feenicia.memphis.commom.soter.ws.JwtDecodeService;
import com.mx.feenicia.memphis.commom.soter.ws.JwtVerifyService;
import com.mx.feenicia.memphis.common.authentication.ClientUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private static final String TYPE_SYSTEM_CLIENT = "CLIENT";


    private  final JwtDecodeService jwtDecodeService;

    private  final JwtVerifyService jwtVerifyService;

    private  final  TblMerchantCredentialRepository merchantCredentialRepository;

    private final TblMerchantRepository merchantRepository;

    @Value("${jwt.header.name}")
    private String jwtHeaderName;

    @Value("${servicio.soter.secret}")
    private String secret;


    public JwtRequestFilter(JwtDecodeService jwtDecodeService, JwtVerifyService jwtVerifyService, TblMerchantCredentialRepository merchantCredentialRepository, TblMerchantRepository merchantRepository) {
        this.jwtDecodeService = jwtDecodeService;
        this.jwtVerifyService = jwtVerifyService;
        this.merchantCredentialRepository = merchantCredentialRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader(jwtHeaderName);

        if (jwt == null) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        Optional<ClientUser> optionalClientUserVo = getCustomClientUserFromJWT(jwt);

        if (optionalClientUserVo.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            SecurityContextHolder.clearContext();
            return;
        }

        ClientUser clientUser = optionalClientUserVo.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(TYPE_SYSTEM_CLIENT));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(clientUser, clientUser, authorities);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);

    }

    private Optional<ClientUser> getCustomClientUserFromJWT(String jwt) {

        Optional<JwtDecodeResponse> optionalJwtDecodeResponse = jwtDecode(jwt);

        if (optionalJwtDecodeResponse.isEmpty()) {
            return Optional.empty();
        }

        JwtDecodeResponse jwtDecodeResponse = optionalJwtDecodeResponse.get();

        JwtPayloadBuilder jwtPayloadBuilder = new JwtPayloadBuilder(jwtDecodeResponse.getPayload());

        log.info("Credentials for {} with the affiliation {}.", jwtPayloadBuilder.getMerchantId(), jwtPayloadBuilder.getAffiliation());

        Optional<TblMerchant> merchantEntity = merchantRepository.findByMerchantAndAffiliation(jwtPayloadBuilder.getMerchantId(), jwtPayloadBuilder.getAffiliation());

        if (merchantEntity.isEmpty()) {
            return Optional.empty();
        }

        Optional<TblMerchantCredential> container = merchantCredentialRepository.findById(merchantEntity.get().getTblMerchantCredential().getId());

        if (container.isEmpty()) {
            return Optional.empty();
        }

        log.info("Credentials for {} with the affiliation {} were found.", jwtPayloadBuilder.getMerchantId(), jwtPayloadBuilder.getAffiliation());

        TblMerchantCredential merchantCredentials = container.get();

        Optional<JwtVerifyResponse> optionalJwtVerifyResponse = jwtVerify(jwt, secret);

        if (optionalJwtVerifyResponse.isEmpty()) {
            return Optional.empty();
        }

        ClientUser clientUserVo = new ClientUser();
        clientUserVo.setMerchantId(jwtPayloadBuilder.getMerchantId());
        clientUserVo.setPublicMerchantId(merchantCredentials.getPublicMerchantId());
        clientUserVo.setPrivateMerchantId(merchantCredentials.getPrivateMerchantId());

        return Optional.of(clientUserVo);
    }

    private Optional<JwtDecodeResponse> jwtDecode(String jwt) {

        JwtDecodeRequest jwtDecodeRequest = jwtDecodeService.buildRequest(jwt);
        JwtDecodeResponse jwtDecodeResponse = jwtDecodeService.jwtDecode(jwtDecodeRequest);

        log.info("Soter JWT Decode: {}", jwtDecodeResponse.getResponseCode());

        if (!jwtDecodeResponse.getResponseCode().equals(SoterResponseCodes.SUCCESS_OPERATION.getResponseCode())) {
            return Optional.empty();
        }

        return Optional.of(jwtDecodeResponse);
    }

    private Optional<JwtVerifyResponse> jwtVerify(String jwt, String secret) {

        JwtVerifyRequest jwtVerifyRequest = jwtVerifyService.builRequest(jwt, secret);
        JwtVerifyResponse jwtVerifyResponse = jwtVerifyService.jwtVerify(jwtVerifyRequest);

        log.info("Soter JWT Verify: {}", jwtVerifyResponse.getResponseCode());

        if (!jwtVerifyResponse.getResponseCode().equals(SoterResponseCodes.SUCCESS_OPERATION.getResponseCode()) || !jwtVerifyResponse.isValid()) {
            return Optional.empty();
        }

        return Optional.of(jwtVerifyResponse);
    }
}
