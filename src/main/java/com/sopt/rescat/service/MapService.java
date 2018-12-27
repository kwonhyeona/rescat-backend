package com.sopt.rescat.service;

import com.sopt.rescat.domain.Cat;
import com.sopt.rescat.domain.MapRequest;
import com.sopt.rescat.domain.Place;
import com.sopt.rescat.dto.MapRequestDto;
import com.sopt.rescat.dto.MarkerListDto;
import com.sopt.rescat.repository.CatRepository;
import com.sopt.rescat.repository.MapRequestRepository;
import com.sopt.rescat.repository.PlaceRepository;
import com.sopt.rescat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MapService {
    private final CatRepository catRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final MapRequestRepository mapRequestRepository;

    public MapService(CatRepository catRepository, PlaceRepository placeRepository, UserRepository userRepository,final MapRequestRepository mapRequestRepository) {
        this.catRepository = catRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.mapRequestRepository = mapRequestRepository;
    }

    public MarkerListDto findMarkerByRegion(final String region){
        List<Cat> cats = catRepository.findByRegion(region);
        List<Place> places = placeRepository.findByRegion(region);

        List<Place> hospitals = new ArrayList<>();
        List<Place> soupKitchens = new ArrayList<>();

        places.forEach(place -> {
            if(place.getCategory()==1)
                hospitals.add(place);
            else
                soupKitchens.add(place);
        });


        return MarkerListDto.builder().cats(cats).hospitals(hospitals).soupKitchens(soupKitchens).build();
    }

    public MapRequest saveCatRequest(final Long userIdx, final MapRequestDto mapRequestDto){
        return mapRequestRepository.save(mapRequestDto.toMapRequest(userIdx));
    }
}
