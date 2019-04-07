package br.com.codenation.hospital.resource;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import br.com.codenation.hospital.resource.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Patient;
import br.com.codenation.hospital.services.PatientService;
import br.com.codenation.hospital.services.HospitalService;

@RestController
@RequestMapping(path = Constant.V1Path)
public class PatientResource {

	@Autowired
	private PatientService service;
	
	@Autowired
	private HospitalService hospitalService;


	@GetMapping(path="/pacientes")
	public List<Patient> findPatients(@PathVariable String hospital_id){
		Hospital obj = hospitalService.findById(hospital_id);
		List<Patient> patientList = obj.getPatients();

		if(patientList != null){
			return patientList;
		}
		throw new ResourceNotFoundException("Hospital sem pacientes!");
	}

	@GetMapping(path="/pacientes/{patientId}")
	public Patient findPatientById(@PathVariable String hospital_id, @PathVariable String patientId){
		Hospital hospital = hospitalService.findById(hospital_id);
		Patient patient = service.findById(patientId);

		return hospital.hasPatient(patient);
	}

	@PostMapping(path="/pacientes/checkin", produces="application/json")
	public Patient checkinPacient(@PathVariable("hospital_id") String idHospital, @RequestBody Patient patient){
		Hospital hospital = hospitalService.findById(idHospital);
		return hospitalService.checkIn(hospital, patient);
	}

	@PostMapping(path="/pacientes/checkout", produces="application/json")
	public Patient checkoutPacient(@PathVariable("hospital_id") String idHospital, @RequestBody String idPatient){
		Hospital hospital = hospitalService.findById(idHospital);
		return hospitalService.checkOut(hospital, idPatient);
	}

	@PutMapping(path = "/pacientes/{patientId}")
	public void updatePatient(@PathVariable("hospital_id") String idHospital, @PathVariable String patientId, @RequestBody Patient patient){
		Hospital hospital = hospitalService.findById(idHospital);
		Patient p = service.findById(patientId);

		p.setName(patient.getName());
		p.setCpf(patient.getCpf());
		p.setBirthDate(patient.getBirthDate());
		p.setGender(patient.getGender());

		service.update(p);
	}
}