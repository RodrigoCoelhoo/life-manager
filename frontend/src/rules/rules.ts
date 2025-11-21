export type Rule = (value: string) => true | string;

export const passwordRules = (): Rule[] => [
	(v) => (!!v && v.trim().length > 0) || "Password is required",
	(v) => (v.length >= 8 && v.length <= 100) || "Password must have between 8 and 100 characters",
	(v) => /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&\-_])[A-Za-z\d@$!%*?&\-_]+$/.test(v) ||
		"Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&-_)",
];

export const usernameRules = (): Rule[] => [
	(v) => (!!v && v.trim().length > 0) || "Username cannot be blank",
	(v) => (v.length >= 3 && v.length <= 50) || "Username must be between 3 and 50 characters long",
	(v) => /^[A-Za-z0-9_-]+$/.test(v) || "Username can only contain letters, numbers, underscores, and hyphens",
];

export const personalNameRules = (): Rule[] => [
	(v) => (!!v && v.trim().length > 0) || "Name cannot be blank",
	(v) => (v.length >= 2 && v.length <= 50) || "Name must be between 2 and 50 characters long",
	(v) => /^\p{L}+$/u.test(v) || "Name can only contain letters",
];

export const emailRules = (): Rule[] => [
	(v) => (!!v && v.trim().length > 0) || "Email is required",
	(v) =>
		/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || "Email must be a valid email address",
];

export const descriptionRules = (): Rule[] => [
	(v) => (v.length <= 512) || "Description cannot exceed 512 characters",
]

export const urlRules = (): Rule[] => [
	(v) => (
		v.length === 0 || /^https?:\/\/[\w.-]+(\.[\w\.-]+)+([\/?].*)?$/.test(v)
	) || "URL must be a valid URL starting with http:// or https://",

	(v) => (v.length <= 2048) || "URL cannot exceed 2048 characters",
]

export const nameRules = (): Rule[] => [
	(v) => (!!v && v.trim().length > 0) || "Name cannot be blank",
	(v) => (v.length <= 50) || "Name cannot exceed 50 characters",
];