package g2o.hdi.hub.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.interceptor.auth.IAuthRule;
import ca.uhn.fhir.rest.server.interceptor.auth.RuleBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.List;


public class MyAuthorizationInterceptor extends ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor {

    //Query the email to a corresponding accessible patient record and tie that to the user
    private String user = "dtalik@yahoo.com";

    @Override
    public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {

        // Process authorization header - The following is a fake
        // implementation. Obviously we'd want something more real
        // for a production scenario.
        //
        // In this basic example we have two hardcoded bearer tokens,
        // one which is for a user that has access to one patient, and
        // another that has full access.
        IdType userIdPatientId = null;
        boolean userIsAdmin = false;
        String e ="";

        String authHeader = theRequestDetails.getHeader("Authorization");

        if(authHeader == null){
            userIsAdmin = true;
        }
        else if(authHeader != null) {

            String token = authHeader.replace("Bearer ", "");
            try {
                DecodedJWT jwt = JWT.decode(token);
                Claim claim = jwt.getClaim("preferred_username");
                e = claim.asString();
                //sub= jwt.toString();
            } catch (JWTDecodeException exception) {
                exception.getMessage();
            }

            if (e.equals(user)) {
                // TODO: Make this more dynamic and query the existing patient records to the email. Right now this user has access only to Patient/1 resources

                userIdPatientId = new IdType("Patient", 1L);
            } else if ("Bearer 39ff939jgg".equals(authHeader)) {
                // This user has access to everything
                userIsAdmin = true;
            } else {
                // Throw an HTTP 401
                throw new AuthenticationException("Missing or invalid Authorization header value");
            }
        }

        // If the user is a specific patient, we create the following rule chain:
        // Allow the user to read anything in their own patient compartment
        // Allow the user to write anything in their own patient compartment
        // If a client request doesn't pass either of the above, deny it
        if (userIdPatientId != null) {
            return new RuleBuilder()
                    .allow().read().allResources().inCompartment("Patient", userIdPatientId).andThen()
                    //.allow().read().allResources().inCompartment("Organization", userIdPatientId).andThen()
                    .allow().write().allResources().inCompartment("Patient", userIdPatientId).andThen()
                    .denyAll()
                    .build();
        }

        // If the user is an admin, allow everything
        if (userIsAdmin) {
            return new RuleBuilder()
                    .allowAll()
                    .build();
        }

        // By default, deny everything. This should never get hit, but it's
        // good to be defensive
        return new RuleBuilder()
                .denyAll()
                .build();
    }
}

