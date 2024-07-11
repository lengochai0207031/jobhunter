package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.DTO.Meta;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkills(Skill skill) {
        return this.skillRepository.save(skill);
    }

    // check name nha ban can chu ý đóa kkk
    public Boolean isNameExit(String name) {
        return this.skillRepository.existsByName(name);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skills = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(skills.getTotalPages());
        mt.setTotal(skills.getTotalPages());
        rs.setMeta(mt);
        rs.setResult(skills.getContent());
        return rs;
    }

    public Skill updateSkill(Skill skill) {
        Optional<Skill> updateSkill = this.skillRepository.findById(skill.getId());
        if (updateSkill.isPresent()) {

            Skill skillUpdate = updateSkill.get();
            skillUpdate.setName(skill.getName());
            return skillUpdate;
        }
        return this.skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill skill = skillOptional.get();
        skill.getJobs().forEach(job -> job.getSkills().remove(skill));
        this.skillRepository.delete(skill);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent())
            return skillOptional.get();
        return null;
    }
}
