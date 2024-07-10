package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.DTO.Meta;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO getAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();
        mt.setPage(pageable.getPageNumber());
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    public Optional<Company> findCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }

    public Optional<Company> handleUpdateCompany(Company company) throws IdInvalidException {
        Optional<Company> currentCompanies = this.companyRepository.findById(company.getId());

        if (!currentCompanies.isPresent()) {
            throw new IdInvalidException("Company with id " + company.getId() + " is not found");
        }

        Company existingCompany = currentCompanies.get();
        existingCompany.setName(company.getName());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setDescription(company.getDescription());
        existingCompany.setLogo(company.getLogo());

        Company updatedCompany = this.companyRepository.save(existingCompany);

        return Optional.of(updatedCompany);
    }

    public void deleteCompany(Long id) {
        this.companyRepository.deleteById(id);
    }
}
