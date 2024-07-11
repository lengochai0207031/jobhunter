package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create a companies")
    public ResponseEntity<Company> postCreateCompany(@Valid @RequestBody Company company) {

        Company companyCreate = this.companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyCreate);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.getAllCompany(spec, pageable));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete a company")
    public ResponseEntity<Void> deleteCompanies(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Company> cuCompany = this.companyService.findCompanyById(id);
        if (!cuCompany.isPresent()) {
            throw new IdInvalidException("Company with id " + id + " is not found");
        }
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/companies")
    @ApiMessage("Update a company")
    public ResponseEntity<Company> putCompanies(@RequestBody Company company) {
        Optional<Company> updatedCompany;

        try {
            updatedCompany = companyService.handleUpdateCompany(company);

            if (!updatedCompany.isPresent()) {
                throw new IdInvalidException("Company with id " + company.getId() + " is not found");
            }

            return ResponseEntity.ok(updatedCompany.get());
        } catch (IdInvalidException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
