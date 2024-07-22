package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.requests.Meta;
import vn.hoidanit.jobhunter.domain.requests.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;
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

    public Optional<Company> findCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public void handleDelete(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();
            List<User> companyUsers = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(companyUsers);
            this.companyRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Company with id " + id + " not found");
        }
    }

}
