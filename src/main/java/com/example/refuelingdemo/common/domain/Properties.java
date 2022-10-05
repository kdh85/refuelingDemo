package com.example.refuelingdemo.common.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.example.refuelingdemo.api.domain.BaseWriter;
import com.example.refuelingdemo.common.domain.converter.TypeConverter;
import com.example.refuelingdemo.common.enums.Type;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = {"parent", "child"})
@EqualsAndHashCode(callSuper = false, exclude = {"parent", "child"})
@Getter
@Entity
@NoArgsConstructor
public class Properties extends BaseWriter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Convert(converter = TypeConverter.class)
	private Type type;

	private String description;

	private String settingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private Properties parent;

	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Properties> child = new ArrayList<>();

	@Builder
	public Properties(Type type, String description, String settingValue, Properties parent,
		List<Properties> child) {
		this.type = type;
		this.description = description;
		this.settingValue = settingValue;
		this.parent = parent;
		this.child = child;
	}

	public static Properties createParentProperties(final Type propertyType, final String description, final String settingValue) {
		return Properties.builder()
			.type(propertyType)
			.description(description)
			.settingValue(settingValue)
			.build();
	}

	public static Properties createChildProperties(final Type propertyType, final String description, final String settingValue,
		final Properties parent) {
		return Properties.builder()
			.type(propertyType)
			.description(description)
			.settingValue(settingValue)
			.build()
			.addChildProperties(parent);
	}

	private Properties addChildProperties(Properties parent) {
		if (Optional.ofNullable(parent).isPresent()) {
			this.parent = parent;
			parent.setChild(List.of(this));
			return this;
		}
		throw new IllegalArgumentException("부모값이 null일 수 없습니다.");
	}

	public Long getSettingValueByLong() {
		return Long.parseLong(this.settingValue);
	}
}
