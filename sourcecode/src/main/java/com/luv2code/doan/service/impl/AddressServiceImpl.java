package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Address;
import com.luv2code.doan.entity.District;
import com.luv2code.doan.entity.Province;
import com.luv2code.doan.entity.Ward;
import com.luv2code.doan.exceptions.AddressNotFoundException;
import com.luv2code.doan.exceptions.NotFoundException;
import com.luv2code.doan.repository.AddressRepository;
import com.luv2code.doan.repository.DistrictRepository;
import com.luv2code.doan.repository.ProvinceRepository;
import com.luv2code.doan.repository.WardRepository;
import com.luv2code.doan.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;


    public List<Province> getListProvinces() {
        return provinceRepository.findAll();
    }

    public List<District> getListDistrict() {
        return districtRepository.findAll();
    }

    public List<Ward> getListWard() {
        return wardRepository.findAll();
    }

    public Ward getWardByCode(String code) throws NotFoundException {
        Ward ward = wardRepository.findWardByCode(code);
        if(ward == null) {
            throw new NotFoundException("Could not find any ward with code " + code);
        }
        return ward;
    }

    public District getDistrictByCode(String code) throws NotFoundException {
        District district =  districtRepository.getDistrictByCode(code);
        if(district == null) {
            throw new NotFoundException("Could not find any district with code " + code);
        }
        return district;
    }

    public Province getProvinceByCode(String code) throws NotFoundException {
        Province province = provinceRepository.getProvinceByCode(code);
        if(province == null) {
            throw new NotFoundException("Could not find any province with code " + code);
        }
        return province;
    }

    public List<District> getListDistrictByProvince(String provinceCode) throws NotFoundException {
        Province province = getProvinceByCode(provinceCode);
        return districtRepository.findDistrictByProvinceCode(province.getCode());
    }

    public List<Ward> getListWardByDistrict(String districtCode) throws NotFoundException {
        District district = getDistrictByCode(districtCode);
        return wardRepository.findWardByDistrictCode(district.getCode());
    }

    public List<Address> getListAddressByUserId(Integer id) {
        return addressRepository.getAddressByUserId(id);
    }

    public Address getAddressDefaultByUserId(Integer id) {
        return addressRepository.getAddressDefaultByUserId(id);
    }

    public long getCountAddressByUserId(Integer id) {
        return addressRepository.countAddressByUserId(id);
    }

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public void setDefaultAddress(Integer addressId,Integer userId) {
        addressRepository.setDefaultAddress(addressId);
        addressRepository.setNonDefaultForOthers(addressId, userId);
    }

    public void delete(Integer addressId, Integer userId) {
        addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    public Address getAddress(Integer addressId, Integer userId) throws AddressNotFoundException {
        try {
            return addressRepository.findByIdAndUserId(addressId, userId);
        }
        catch(NoSuchElementException ex) {
            throw new AddressNotFoundException("Could not find any address with ID " + addressId);

        }
    }
}
