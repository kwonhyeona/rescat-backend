package com.sopt.rescat.service;

import com.sopt.rescat.domain.*;
import com.sopt.rescat.domain.enums.Role;
import com.sopt.rescat.dto.*;
import com.sopt.rescat.exception.InvalidValueException;
import com.sopt.rescat.exception.NotFoundException;
import com.sopt.rescat.exception.NotMatchException;
import com.sopt.rescat.exception.UnAuthenticationException;
import com.sopt.rescat.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MapService {
    private final CatRepository catRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final MapRequestRepository mapRequestRepository;
    private final RegionRepository regionRepository;
    private final S3FileService s3FileService;
    private final PhotoRepository photoRepository;

    public MapService(CatRepository catRepository,
                      PlaceRepository placeRepository,
                      UserRepository userRepository,
                      MapRequestRepository mapRequestRepository,
                      RegionRepository regionRepository,
                      S3FileService s3FileService,
                      PhotoRepository photoRepository) {
        this.catRepository = catRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.mapRequestRepository = mapRequestRepository;
        this.regionRepository = regionRepository;
        this.s3FileService = s3FileService;
        this.photoRepository = photoRepository;
    }

    public User getUser(final Long userIdx){
        User user = userRepository.findByIdx(userIdx);
        if(!(user.getRole() == Role.CARETAKER)){
            throw new UnAuthenticationException("user", "케어테이커 인증을 받지 않은 사용자입니다.");
        }
        return user;
    }

    public MarkerListDto getMarkerListByRegion(final User user, final Optional<Integer> emdCode) {

        Region selectedRegion = user.getMainRegion();
        if(emdCode.isPresent()) {
            if(!getRegionList(user).stream().anyMatch(regionDto -> regionDto.getCode()==emdCode.get())){
                throw new UnAuthenticationException("emdCode", "인가되지 않은 지역입니다.");
            }
            selectedRegion = regionRepository.findByemdCode(emdCode.get()).orElseThrow(() -> new NotFoundException("emdCode", "지역을 찾을 수 없습니다."));
        }

        List<CatDto> cats = catRepository.findByRegion(selectedRegion).stream().map(cat -> cat.toCatDto()).collect(Collectors.toList());
        List<Place> places = placeRepository.findByRegion(selectedRegion);

        List<PlaceDto> hospitals = new ArrayList<>();
        List<PlaceDto> feeders = new ArrayList<>();

        places.forEach(place -> {
            boolean placeType = (place.getCategory() != 0);
            if (placeType) {
                hospitals.add(place.toPlaceDto());
            } else {
                feeders.add(place.toPlaceDto());
            }
        });

        return MarkerListDto.builder()
                .cats(cats).hospitals(hospitals).feeders(feeders).build();
    }
//
//    public MapRequest saveRequestCatRegister(final Long userIdx, final MapRequestDto mapRequestDto) {
//        User user = userRepository.findByIdx(userIdx);
//        return mapRequestRepository.save(mapRequestDto.toMapRequest(user));
//    }
//
//    public void saveRequestPlaceRegister(final Long userIdx, final MapRequestDto mapRequestDto){
//        // registerType이 1이거나 2이어야만 함.
//        User user = userRepository.findByIdx(userIdx);
//
//       // mapRequestRepository.save(mapRequestDto.toMapRequest(user));

    @Transactional
    public void saveMarkerRequest(final User user, final MapRequestDto mapRequestDto) throws IOException {
        Photo markerPhoto = photoRepository.findByIdx(Photo.DEFAULT_PHOTO_ID).orElseThrow(NotFoundException::new);
        if(mapRequestDto.getPhoto()!=null)
            markerPhoto = photoRepository.save(new Photo(s3FileService.upload(mapRequestDto.getPhoto())));

        mapRequestRepository.save(mapRequestDto.toMapRequest(user, markerPhoto));
    }

    public List<RegionDto> getRegionList(final User user) {

        List<Region> regions = new ArrayList<>();
        regions.add(user.getMainRegion());
        regions.add(user.getSubRegion1());
        regions.add(user.getSubRegion2());

        return regions.stream().filter(Objects::nonNull)
                .map(region -> region.toRegionDto())
                .collect(Collectors.toList());
    }

//    public List<List<MapRequestDto>> getMapRequestList(final Long userIdx) {
//        // 관리자이면 전체보기
//        User user = userRepository.findByIdx(userIdx);
//        if (!(user.getRole() == Role.ADMIN)) {
//            throw new UnAuthenticationException("user", "관리자 권한이 필요합니다.");
//        }
//
//
//
//    }

}
