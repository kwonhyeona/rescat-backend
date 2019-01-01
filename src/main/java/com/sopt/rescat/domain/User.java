package com.sopt.rescat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopt.rescat.domain.enums.Role;
import com.sopt.rescat.dto.UserLoginDto;
import com.sopt.rescat.exception.InvalidValueException;
import com.sopt.rescat.exception.NotMatchException;
import com.sopt.rescat.exception.UnAuthenticationException;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    @NonNull
    @Length(max = 10)
    private String name;

    @Column(unique = true)
    @NonNull
    @Length(max = 30)
    private String nickname;

    @Column(unique = true)
    @NonNull
    @Length(max = 50)
    private String id;

    @Column
    @Length(max = 11)
    private String phone;

    @Column
    @NonNull
    @Length(max = 300)
    @JsonIgnore
    private String password;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_user_main_region_idx"))
    private Region mainRegion;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_user_sub_1_region_idx"))
    private Region subRegion1;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_user_sub_2_region_idx"))
    private Region subRegion2;

    @Column
    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role;

    @Column
    private String photoUrl;

    @Column
    private Long mileage;

    @Builder
    public User(String id, String password, String nickname) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.role = Role.MEMBER;
    }

    @Builder
    public User(String nickname){
        this.nickname = nickname;
    }

    public boolean matchPasswordBy(UserLoginDto userLoginDto, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(userLoginDto.getPassword(), this.password)) {
            throw new NotMatchException("password", "비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    private void checkMileageMoreThan(Long mileage) {
        if(this.mileage + mileage < 0) throw new InvalidValueException("mileage", "사용자가 가진 마일리지는 음수가 될 수 없습니다.");
    }

    public void updateMileage(Long mileage) {
        checkMileageMoreThan(mileage);
        this.mileage += mileage;
    }

    public boolean isAuthenticatedRegion(Integer emdCode) {
        if (this.mainRegion.getEmdCode() == emdCode || this.subRegion1.getEmdCode() == emdCode || this.subRegion2.getEmdCode() == emdCode)
            return true;
        throw new UnAuthenticationException("emdCode", "인가되지 않은 지역입니다.");
    }

    public void updateUser(String nickname, String phone){
        this.nickname = nickname;
        this.phone = phone;
    }

}
