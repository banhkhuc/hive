package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.OtherSkill;

import java.util.List;
import java.util.Optional;

public interface OtherSkillService {
    List<OtherSkill> getListOtherSkillByCvId(Long cvId);

    OtherSkill insertOtherSkill(OtherSkill newOtherSkill);

    void updateOtherSKill(OtherSkill otherSkill);

    void deleteOtherSkill(OtherSkill otherSkill);

    Optional<OtherSkill> getOtherSkillById(long id);
}
