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
	flag: string;
}

export const CurrencyData: Record<CurrencyCode, CurrencyInfo> = {
	EUR: { name: "Euro", 				symbol: "€", 	flag: "🇪🇺" },
	USD: { name: "US Dollar", 			symbol: "$", 	flag: "🇺🇸" },
	GBP: { name: "British Pound", 		symbol: "£", 	flag: "🇬🇧" },
	BRL: { name: "Brazilian Real", 		symbol: "R$", 	flag: "🇧🇷" },
	JPY: { name: "Japanese Yen", 		symbol: "¥", 	flag: "🇯🇵" },
	AUD: { name: "Australian Dollar", 	symbol: "A$", 	flag: "🇦🇺" },
	CAD: { name: "Canadian Dollar", 	symbol: "C$", 	flag: "🇨🇦" },
	CHF: { name: "Swiss Franc", 		symbol: "CHF", 	flag: "🇨🇭" },
	CNY: { name: "Chinese Yuan", 		symbol: "¥", 	flag: "🇨🇳" },
	SEK: { name: "Swedish Krona", 		symbol: "kr", 	flag: "🇸🇪" },
	NZD: { name: "New Zealand Dollar", 	symbol: "NZ$", 	flag: "🇳🇿" },
	MXN: { name: "Mexican Peso", 		symbol: "$", 	flag: "🇲🇽" },
	SGD: { name: "Singapore Dollar", 	symbol: "S$", 	flag: "🇸🇬" },
	HKD: { name: "Hong Kong Dollar", 	symbol: "HK$", 	flag: "🇭🇰" },
	NOK: { name: "Norwegian Krone", 	symbol: "kr", 	flag: "🇳🇴" },
	KRW: { name: "South Korean Won", 	symbol: "₩", 	flag: "🇰🇷" },
	TRY: { name: "Turkish Lira", 		symbol: "₺", 	flag: "🇹🇷" },
	INR: { name: "Indian Rupee", 		symbol: "₹", 	flag: "🇮🇳" },
	RUB: { name: "Russian Ruble",		symbol: "₽", 	flag: "🇷🇺" },
	ZAR: { name: "South African Rand", 	symbol: "R", 	flag: "🇿🇦" },
};



export const formatBalance = (value: string) => {
	const [int, dec] = value.split(".");
	const spaced = int.replace(/\B(?=(\d{3})+(?!\d))/g, " ");
	return dec ? `${spaced}.${dec}` : spaced;
};
