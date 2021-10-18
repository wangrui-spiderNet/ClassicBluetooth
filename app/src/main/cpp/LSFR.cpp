#include "LSFR.h"

#define  POLY_LFSR16    0xD103

using namespace SmartLinkCore;
 
uint16_t CLFSR::Loop_Key(uint16_t key)
{
	uint16_t l_temp;
	if (key & 0x8000) {
		l_temp = POLY_LFSR16;
	}
	else {
		l_temp = 0x0000;
	}

	key = (((key << 1) ^ l_temp) | (key >> 15));
	return key;
}

uint16_t CLFSR::Encrypt_OneByte(uint8_t* l_data, uint16_t key)
{
	int i;
	uint16_t l_key;
	uint8_t l_encdata = 0;
	for (i = 8; i > 0; i--) {
		l_encdata <<= 1;
		l_encdata |= ((*l_data >> (i - 1)) ^ (key >> 15)) & 0x01;

		key = Loop_Key(key);
	}
	*l_data = l_encdata;
	l_key = key;

	return l_key;
}

void CLFSR::Encrypt_Data(uint16_t init_key, uint8_t *l_data, uint32_t length)
{
	uint32_t i;
	uint16_t key = init_key;
	for (i = 0; i < length; i++) {
		key = Encrypt_OneByte(&l_data[i], key);
	}
}