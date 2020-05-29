package g2o.hdi.hub.provider;

import java.util.*;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

public class PatientProvider implements IResourceProvider {

   private Map<Long, Patient> myPatients = new LinkedHashMap<>();
   private long myNextId = 1;

   /**
    * Constructor
    */
   public PatientProvider() {
//      Patient pat1 = new Patient();
//      pat1.setId(""+(myNextId++));
//      pat1.addIdentifier().setSystem("http://acme.com/MRNs").setValue("7000135");
//      pat1.addName().setFamily("Simpson").addGiven("Homer").addGiven("J");
//      myPatients.put("1", pat1);
   }

   @Override
   public Class<? extends IBaseResource> getResourceType() {
      return Patient.class;
   }

   /**
    * Simple implementation of the "read" method
    */
   @Read()
   public Patient read(@IdParam IdType theId) {
      Patient retVal = myPatients.get(Long.parseLong(theId.getIdPart()));
      if (retVal == null) {
         throw new ResourceNotFoundException(theId);
      }
      return retVal;
   }

   /**
    * The "@Create" annotation indicates that this method implements "create=type", which adds a
    * new instance of a resource to the server.
    */
   @Create()
   public MethodOutcome createPatient(@ResourceParam Patient thePatient) {

      // Here we are just generating IDs sequentially
      long id = myNextId++;
      IdType newId = new IdType("Patient", Long.toString(id));
      thePatient.setId(newId);
      
      myPatients.put(id, thePatient);

      myPatients = myPatients.entrySet()
              .stream()
              .sorted(Map.Entry.comparingByKey())
              .collect(Collectors.toMap(
                      Map.Entry::getKey,
                      Map.Entry::getValue,(oldValue,newValue)->oldValue, LinkedHashMap::new));

      return new MethodOutcome(new IdType(id));
   }
   
   @Search
   public List<Patient> findPatientsUsingArbitraryCtriteria() {
      return myPatients.values().stream().collect(Collectors.toList());
   }

   

}
