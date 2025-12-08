export const CurrencyCode = {
	EUR: "EUR",
	USD: "USD",
	GBP: "GBP",
	BRL: "BRL",
	JPY: "JPY",
	AUD: "AUD",
	CAD: "CAD",
	CHF: "CHF",
	CNY: "CNY",
	SEK: "SEK",
	NZD: "NZD",
	MXN: "MXN",
	SGD: "SGD",
	HKD: "HKD",
	NOK: "NOK",
	KRW: "KRW",
	TRY: "TRY",
	INR: "INR",
	RUB: "RUB",
	ZAR: "ZAR",
} as const;
export type CurrencyCode = keyof typeof CurrencyCode;

export interface CurrencyInfo {
	name: string;
	symbol: string;
}

export const CurrencyData: Record<CurrencyCode, CurrencyInfo> = {
	EUR: { name: "Euro", 				symbol: "€"},
	USD: { name: "US Dollar", 			symbol: "$"},
	GBP: { name: "British Pound", 		symbol: "£"},
	BRL: { name: "Brazilian Real", 		symbol: "R$"},
	JPY: { name: "Japanese Yen", 		symbol: "¥"},
	AUD: { name: "Australian Dollar", 	symbol: "A$"},
	CAD: { name: "Canadian Dollar", 	symbol: "C$"},
	CHF: { name: "Swiss Franc", 		symbol: "CHF"},
	CNY: { name: "Chinese Yuan", 		symbol: "¥"},
	SEK: { name: "Swedish Krona", 		symbol: "kr"},
	NZD: { name: "New Zealand Dollar", 	symbol: "NZ$"},
	MXN: { name: "Mexican Peso", 		symbol: "$"},
	SGD: { name: "Singapore Dollar", 	symbol: "S$"},
	HKD: { name: "Hong Kong Dollar", 	symbol: "HK$"},
	NOK: { name: "Norwegian Krone", 	symbol: "kr"},
	KRW: { name: "South Korean Won", 	symbol: "₩"},
	TRY: { name: "Turkish Lira", 		symbol: "₺"},
	INR: { name: "Indian Rupee", 		symbol: "₹"},
	RUB: { name: "Russian Ruble",		symbol: "₽"},
	ZAR: { name: "South African Rand", 	symbol: "R"},
};



export const formatBalance = (value: string) => {
	const [int, dec] = value.split(".");
	const spaced = int.replace(/\B(?=(\d{3})+(?!\d))/g, " ");
	return dec ? `${spaced}.${dec}` : spaced;
};