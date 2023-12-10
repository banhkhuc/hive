package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.OtherSkill;
import fpt.edu.capstone.repository.OtherSkillRepository;
import fpt.edu.capstone.service.OtherSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OtherSkillServiceImpl implements OtherSkillService {

    @Autowired
    OtherSkillRepository otherSkillRepository;

    @Override
    public List<OtherSkill> getListOtherSkillByCvId(Long cvId) {
        return otherSkillRepository.getListOtherSkillByCvId(cvId);
    }

    @Override
    public OtherSkill insertOtherSkill(OtherSkill newOtherSkill) {
        return otherSkillRepository.save(newOtherSkill);
    }

    @Override
    public void updateOtherSKill(OtherSkill otherSkill) {
        otherSkillRepository.updateOtherSkill(otherSkill.getSkillName(), otherSkill.getLevel(), otherSkill.getId());
    }

    @Override
    public void deleteOtherSkill(OtherSkill otherSkill) {
        otherSkillRepository.deleteOtherSkill(otherSkill.getId());
    }

    @Override
    public Optional<OtherSkill> getOtherSkillById(long id) {
        return  otherSkillRepository.findById(id);
    }
}
