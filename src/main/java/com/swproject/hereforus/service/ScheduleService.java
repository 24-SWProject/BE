package com.swproject.hereforus.service;

import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.dto.ScheduleDto;
import com.swproject.hereforus.entity.Bookmark;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.Schedule;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.GroupRepository;
import com.swproject.hereforus.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final UserDetailService userDetailService;
    private final GroupService groupService;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final ModelMapper modelMapper;

    public Object saveSchedule(ScheduleDto scheduleDto) {
//        User user = userDetailService.getAuthenticatedUserId();
//        Optional<Group> group = groupService.findGroupForUser(user.getId());
        Optional<Group> group = groupService.findGroupForUser(Long.valueOf("1"));
        Schedule schedule = Schedule.builder()
                .group(group.get())
                .content(scheduleDto.getContent())
                .date(scheduleDto.getScheduleDate())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return savedSchedule;
    }

    public Object updateSchedule(ScheduleDto scheduleDto, Long id) {
//        User user = userDetailService.getAuthenticatedUserId();
//        Optional<Group> group = groupService.findGroupForUser(user.getId());
        Optional<Group> group = groupService.findGroupForUser(Long.valueOf("1"));

        Optional<Schedule> existingSchedule = scheduleRepository.findById(id);
        existingSchedule.get().setContent(scheduleDto.getContent());

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule.get());

        return updatedSchedule;
    }

    public Object deleteSchedule(Long id) {
//        User user = userDetailService.getAuthenticatedUserId();
//        Optional<Group> group = groupService.findGroupForUser(user.getId());
        Optional<Group> group = groupService.findGroupForUser(Long.valueOf("1"));

        Optional<Schedule> existingSchedule = scheduleRepository.findById(id);
        if (existingSchedule.isPresent() && existingSchedule.get().getGroup().equals(group.get())) {
            scheduleRepository.delete(existingSchedule.get());
        }

        return "일정이 삭제되었습니다.";
    }

    public List<ScheduleDto> selectSchedule() {
//        User user = userDetailService.getAuthenticatedUserId();
//        Optional<Group> group = groupService.findGroupForUser(user.getId());
        Optional<Group> group = groupService.findGroupForUser(Long.valueOf("1"));

        List<Schedule> existingSchedules = scheduleRepository.findAllByGroup(group.get());

        // 리스트 변환
        return existingSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDto.class))
                .collect(Collectors.toList());
    }
}
