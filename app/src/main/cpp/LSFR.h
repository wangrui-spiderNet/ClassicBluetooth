#pragma once
#include <stdint.h>

namespace SmartLinkCore
{
	class CLFSR
	{
	public:
		CLFSR(){};
		~CLFSR(){};
	public:
		void Encrypt_Data(uint16_t init_key, uint8_t *l_data, uint32_t length);
	private:
		uint16_t Loop_Key(uint16_t key);
		uint16_t Encrypt_OneByte(uint8_t* l_data, uint16_t key);
	};
};