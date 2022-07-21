prompt PL/SQL Developer Export Tables for user DETECTOR_MINES
prompt Created by dodge1 on Wednesday, January 22, 2020
set feedback off
set define off

prompt Disabling triggers for STORED_FILTER...
alter table STORED_FILTER disable all triggers;
prompt Loading STORED_FILTER...
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (793, 'BP', 'y', 4, .5, 1, 'BP_LC_.5_HC_1_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (853, 'BP', 'y', 4, 1, 3, 'BP_LC_1_HC_3_Causal_Ord_4', 'iir', 'walter', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1572, 'BP', 'y', 4, .01, .1, 'BP_LC_.01_HC_.1_Causal_Ord_4', 'iir', 'dodge', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (995, 'BP', 'y', 4, .05, .1, 'BP_LC_.05_HC_.1_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1363, 'BP', 'y', 4, 12, 16, 'BP_LC_12_HC_16_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (755, 'BP', 'y', 4, 1, 2, 'BP_LC_1_HC_2_Causal_Ord_4', 'iir', 'walter', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1241, 'BP', 'y', 4, 4, 10, 'BP_LC_4_HC_10_Causal_Ord_4', 'iir', 'rodgers', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (2162, 'BP', 'y', 4, .02, .1, 'BP_LC_.02_HC_.1_Causal_Ord_4', 'iir', 'rodgers', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (5067, 'BP', 'y', 8, 2.2, 4.4, 'BP_LC_2.2_HC_4.4_Causal_Ord_8', 'iir', 'flori', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (753, 'BP', 'y', 4, 1, 8, 'BP_LC_1_HC_8_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1201, 'BP', 'y', 4, 1, 4, 'BP_LC_1_HC_4_Causal_Ord_4', 'iir', 'walter', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1341, 'BP', 'y', 4, 3, 6, 'BP_LC_3_HC_6_Causal_Ord_4', 'iir', 'walter', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1364, 'BP', 'y', 4, 10, 12, 'BP_LC_10_HC_12_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1541, 'BP', 'y', 4, 2, 6, 'BP_LC_2_HC_6_Causal_Ord_4', 'iir', 'parker', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1742, 'BP', 'y', 4, .5, 4, 'BP_LC_.5_HC_4_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1802, 'BP', 'y', 4, .5, 16, 'BP_LC_.5_HC_16_Causal_Ord_4', 'iir', 'dodge', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (494, 'BP', 'y', 4, 2, 8, 'BP_LC_2_HC_8_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (495, 'BP', 'y', 4, 6, 8, 'BP_LC_6_HC_8_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1804, 'HP', 'y', 4, 18, 20, 'HP_LC_18_HC_20_Causal_Ord_4', 'iir', 'dodge', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (474, 'BP', 'y', 4, .5, 8, 'BP_LC_.5_HC_8_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (754, 'BP', 'y', 4, 2, 4, 'BP_LC_2_HC_4_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (994, 'BP', 'y', 4, 4, 6, 'BP_LC_4_HC_6_Causal_Ord_4', 'iir', 'walter', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (2001, 'BP', 'y', 4, 2, 16, 'BP_LC_2_HC_16_Causal_Ord_4', 'iir', 'jbonner', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1142, 'BP', 'y', 4, 8, 10, 'BP_LC_8_HC_10_Causal_Ord_4', 'iir', 'dodge', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1165, 'BP', 'y', 2, 1, 3, 'BP_LC_1_HC_3_Causal_Ord_2', 'iir', 'walter', 'n');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (1801, 'BP', 'y', 4, 1, 16, 'BP_LC_1_HC_16_Causal_Ord_4', 'iir', 'dodge', 'y');
insert into STORED_FILTER (filter_id, type, causal, filter_order, lowpass, highpass, description, impulse_response, auth, is_default)
values (4, 'BP', 'y', 4, 4, 20, 'BP_LC_4_HC_20_Causal_Order_4', 'iir', 'detector_rhiannon', 'n');
commit;
prompt 27 records loaded
prompt Enabling triggers for STORED_FILTER...
alter table STORED_FILTER enable all triggers;

set feedback on
set define on
prompt Done
