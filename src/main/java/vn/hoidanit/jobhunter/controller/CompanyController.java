package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> postCreateCompany(@Valid @RequestBody Company company) {
        Company companyCreate = this.companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyCreate);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(

            @RequestParam("current") Optional<String> cureOptional,
            @RequestParam("pareSize") Optional<String> pareOptional) {
        String sCurrent = cureOptional.isPresent() ? cureOptional.get() : "";
        String sPageSize = pareOptional.isPresent() ? pareOptional.get() : "";
        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        ResultPaginationDTO companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(companies);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Optional<Company>> putMethodName(@RequestBody Company company, @PathVariable("id") Long id) {
        Optional<Company> companies = this.companyService.postUpdateCompany(id);
        if (companies.isPresent()) {

            Company companyUpdate = companies.get();

            companyUpdate.setName(company.getName());
            companyUpdate.setAddress(company.getAddress());
            companyUpdate.setDescription(company.getDescription());
            companyUpdate.setLogo(company.getLogo());

        }
        Company updatecompany = this.companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(Optional.of(updatecompany));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompanies(@PathVariable("id") Long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

}
