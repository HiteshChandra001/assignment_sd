package com.smartdatasolutions.test.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.smartdatasolutions.test.Member;
import com.smartdatasolutions.test.MemberExporter;
import com.smartdatasolutions.test.MemberFileConverter;
import com.smartdatasolutions.test.MemberImporter;

public class Main extends MemberFileConverter {

	@Override
	protected MemberExporter getMemberExporter() {
		return new MemberExporterImpl();
	}

	@Override
	protected MemberImporter getMemberImporter() {
		return new MemberImporterImpl();
	}

	@Override
	protected List<Member> getNonDuplicateMembers(List<Member> membersFromFile) {
		Set<String> ids = new HashSet<>();
		return membersFromFile.stream().filter(member -> ids.add(member.getId())) // Only add if id is unique
				.collect(Collectors.toList());
	}

	@Override
	protected Map<String, List<Member>> splitMembersByState(List<Member> validMembers) {
		return validMembers.stream().collect(Collectors.groupingBy(Member::getState));

	}

	public static void main(String[] args) {
		try {
	        Main converter = new Main();
	        MemberImporter importer = converter.getMemberImporter();
	        MemberExporter exporter = converter.getMemberExporter();

	        // Step 1: Import members from the file
	        File inputFile = new File("Members.txt");
	        List<Member> members = importer.importMembers(inputFile);

	        // Step 2: Remove duplicates
	        List<Member> nonDuplicateMembers = converter.getNonDuplicateMembers(members);

	        // Step 3: Split members by state
	        Map<String, List<Member>> membersByState = converter.splitMembersByState(nonDuplicateMembers);

	        // Step 4: Export each state's members to CSV files
	        for (Map.Entry<String, List<Member>> entry : membersByState.entrySet()) {
	            String state = entry.getKey();
	            List<Member> membersInState = entry.getValue();
	            File outputFile = new File(state + "_outputFile.csv");

	            try (Writer writer = new FileWriter(outputFile)) {
	                for (Member member : membersInState) {
	                    exporter.writeMember(member, writer);
	                    writer.write("\n");
	                }
	            }
	        }

	        System.out.println("Conversion completed successfully.");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
