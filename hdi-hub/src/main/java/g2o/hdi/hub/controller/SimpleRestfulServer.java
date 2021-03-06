package g2o.hdi.hub.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import g2o.hdi.hub.interceptor.MyAuthorizationInterceptor;
import g2o.hdi.hub.interceptor.MyCorsInterceptor;
import g2o.hdi.hub.provider.HospitalProvider;
import g2o.hdi.hub.provider.PatientProvider;

@WebServlet("/*")
public class SimpleRestfulServer extends RestfulServer {

	@Override
	protected void initialize() throws ServletException {
		// Create a context for the appropriate version
		setFhirContext(FhirContext.forR4());
		PatientProvider provider = new PatientProvider();
		
		// Register resource providers
		registerProvider(provider);
		registerProvider(new HospitalProvider());
		
		// Format the responses in nice HTML
		registerInterceptor(new ResponseHighlighterInterceptor());
		registerInterceptor(new MyAuthorizationInterceptor(provider));
		
		// enable CORS
		registerInterceptor(new MyCorsInterceptor());
	}
}
