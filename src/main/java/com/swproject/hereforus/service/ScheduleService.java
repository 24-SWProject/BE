package com.swproject.hereforus.service;

import com.swproject.hereforus.dto.ScheduleDto;
import com.swproject.hereforus.dto.group.GroupDto;
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
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.spi.LocaleServiceProvider;
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

    public ScheduleDto saveSchedule(ScheduleDto scheduleDto) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupService.findGroupForUser(user.getId());

        Schedule schedule = Schedule.builder()
                .group(group.get())
                .content(scheduleDto.getContent())
                .date(scheduleDto.getScheduleDate())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(savedSchedule, ScheduleDto.class);
    }

    public ScheduleDto updateSchedule(ScheduleDto scheduleDto, Long id) {
        Optional<Schedule> existingSchedule = scheduleRepository.findById(id);
        existingSchedule.get().setContent(scheduleDto.getContent());

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule.get());
        return modelMapper.map(updatedSchedule, ScheduleDto.class);
    }

    public Object deleteSchedule(Long id) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupService.findGroupForUser(user.getId());

        Optional<Schedule> existingSchedule = scheduleRepository.findById(id);
        if (existingSchedule.isPresent() && existingSchedule.get().getGroup().equals(group.get())) {
            scheduleRepository.delete(existingSchedule.get());
        }

        return "일정이 삭제되었습니다.";
    }

    public List<ScheduleDto> selectSchedule(String date) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupService.findGroupForUser(user.getId());

        List<Schedule> schedules;

        if (date != null && !date.isEmpty()) {
            // 월의 첫날과 마지막 날 계산
            LocalDate startDate = LocalDate.parse(date + "-01");
            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

            // Repository 호출
            schedules = scheduleRepository.findAllByGroupAndDateRange(group.get(), startDate, endDate);
        } else {
            schedules = scheduleRepository.findAllByGroup(group.get());
        }

        // 리스트 변환
        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDto.class))
                .collect(Collectors.toList());
    }
}
