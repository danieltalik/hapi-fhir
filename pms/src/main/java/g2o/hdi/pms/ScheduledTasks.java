package g2o.hdi.pms;

import java.time.LocalDate;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;


@Component
public class ScheduledTasks {
	LocalDate ld=LocalDate.now();
	int charVal = 64;
	
	public void createPatient(Organization org) {
		// Create a patient
				Patient newPatient = buildPatient(org);

				// Create a client
				FhirContext ctx = FhirContext.forR4();
				IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080");

				// Create the resource on the server
				MethodOutcome outcome = client
					.create()
					.resource(newPatient)
					.execute();

				// Log the ID that the server assigned
				IIdType id = outcome.getId();
				
				System.out.println("created is: "+id.getValue());
	
	}

	@Scheduled(fixedRate = 5000)
	public void createOrg() {
		// Create a organization
		Organization newOrg = buildOrginzation();

		// Create a client
		FhirContext ctx = FhirContext.forR4();
		IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080");

		// Create the resource on the server
		MethodOutcome outcome = client
				.create()
				.resource(newOrg)
				.execute();

		// Log the ID that the server assigned
		IIdType id = outcome.getId();

		System.out.println("created is: "+id.getValue());
		newOrg.setId(id);
		createPatient(newOrg);

	}

	private Patient buildPatient(Organization org) {
		Patient newPatient = new Patient();

		// Populate the patient with fake information
		newPatient
			.addName()
				.setFamily("DevDays"+ld.getDayOfMonth())
				.addGiven("John")
				.addGiven("Q"+ld.getDayOfWeek());
		newPatient
			.addIdentifier()
				.setSystem("http://acme.org/mrn")
				.setValue("1234567");

		newPatient.setGender(ld.getDayOfMonth()%2==0?Enumerations.AdministrativeGender.MALE:Enumerations.AdministrativeGender.FEMALE);
		newPatient.setManagingOrganization(new Reference(org.getId()));
		newPatient.setManagingOrganizationTarget(org);
		newPatient.setBirthDateElement(new DateType(ld.toString()));
		return newPatient;
	}

	private Organization buildOrginzation() {
		Organization newOrg = new Organization();
		ld=ld.plusDays(1);
		charVal++;
		// Populate the patient with fake information
		newOrg
				.setName("Hospital " + (char)charVal)
				.setActive(ld.getDayOfMonth() % 2 == 0)
				.addIdentifier()
					.setSystem("http://acme.org/mrn")
					.setValue("1234567");
		newOrg
				.addAddress()
					.setCountry("United States")
					.setPostalCode(Integer.toString(43214 + ld.getDayOfMonth()))
					.setState("OH")
					.setCity("Columbus")
					.setType(Address.AddressType.BOTH)
					.addLine( Integer.toString(ld.getDayOfMonth()) + " " + ld.getDayOfWeek().toString().substring(0,1).toUpperCase()+
							ld.getDayOfWeek().toString().substring(1).toLowerCase() + " Street")
					.setUse(Address.AddressUse.BILLING);
		return newOrg;
	}

}
