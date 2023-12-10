package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.common.CreateRoleUserRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.Fields;
import fpt.edu.capstone.entity.Role;
import fpt.edu.capstone.entity.VietnamCountry;
import fpt.edu.capstone.service.CountryService;
import fpt.edu.capstone.service.FieldsService;
import fpt.edu.capstone.service.RoleService;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/common")
@AllArgsConstructor
public class CommonController {
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    private final FieldsService fieldsService;

    private final CountryService countryService;

    private final RoleService roleService;

    @GetMapping("/get-list-field")
    public ResponseData getListCareer(){
        try {
            List<Fields> careers = fieldsService.getAllField();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, careers);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/get-vietnam-country")
    public ResponseData getVietnamCountry(){
        try {
            List<VietnamCountry> careers = countryService.getListCountry();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, careers);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PostMapping("/create-role")
    public ResponseData createRole(@RequestBody CreateRoleUserRequest request) {
        try {
            Role role = roleService.createRole(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, role);
        }catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-all-role")
    @Operation(summary = "Admin module - get all role in system")
    public ResponseData createRole() {
        try {
            List<Role> role = roleService.getAllRole();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, role);
        }catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }
}
